package org.jeff.web.http;

import org.jeff.core.BinaryBuffer;
import org.jeff.core.DynamicBuffer;
import org.jeff.core.ReadableByteBuffer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class RequestParser
{
    enum RequestParserFSM
    {
        METHOD,
        HEADER,
        BODY,
    }

    private BinaryBuffer _cache_buffer = new BinaryBuffer(1024);
    private RequestParserFSM _state = RequestParserFSM.METHOD;
    private Request _request = new Request();
    private DynamicBuffer _body_buffer = null;
    private int _wait_body_size = -1;
    private boolean _chunked_body = false;

    public RequestParserState parser(ByteBuffer byteBuffer)
    {
        if(_state != RequestParserFSM.BODY)
            _cache_buffer.put(byteBuffer.array(), 0, byteBuffer.remaining());
        else
            _body_buffer.put(byteBuffer.array(), 0, byteBuffer.remaining());
        return this.http_fsm_parser();
    }
    private RequestParserState http_fsm_parser()
    {
        // 解析HTTP的请求内容
        while(_cache_buffer.hasRemaining())
        {
            if (_state == RequestParserFSM.METHOD)  // 解析请求方法
            {
                String line = read_line(this._cache_buffer);
                if (line == null) return RequestParserState.Continue;
                String[] args = line.split(" ");
                if (args.length < 2) return RequestParserState.Error;
                _request.method = args[0].trim().toUpperCase();
                _request.path = args[1].trim();
                if (args.length == 3) {
                    _request.version = args[2].trim();
                } else {
                    _request.version = "HTTP/0.9";
                }
                _state = RequestParserFSM.HEADER;
            } else if (_state == RequestParserFSM.HEADER)   // 读取头数据
            {
                String line = read_line(this._cache_buffer);
                if(line == null) return RequestParserState.Continue;
                if(line.isEmpty())
                {
                    if(!_request.method.equals("POST"))  // 只有POST方法存在body
                    {
                        return parse_request_params();
                    }
                    _state = RequestParserFSM.BODY;
                    continue;
                }
                String[] args = line.split(":");
                if(args.length <2) return RequestParserState.Error;
                _request.headers.put(args[0].trim().toUpperCase(), args[1].trim());
            }else if(_state == RequestParserFSM.BODY)  // 读取body内容 只有POST方法有
            {
                if(this._body_buffer == null)
                {
                    // 将cachebuffer里面还没有读取的数据作为body存入
                    this._body_buffer = new DynamicBuffer(this._cache_buffer.array(), this._cache_buffer.get_readIndex(), this._cache_buffer.remaining());
                }
                if(_wait_body_size < 0)  // 说明还不知道长度
                {
                    if (_chunked_body)  // 如果是chunk的方式，那就读取第一行的十六进制表示长度
                    {
                        String hex_len = read_line(this._body_buffer);
                        if (hex_len == null) return RequestParserState.Continue;
                        _wait_body_size = Integer.parseInt(hex_len, 16);
                    } else {
                        String content_length = _request.get_header("Content-Length");
                        if (content_length == null)  // 没有指定长度
                        {
                            String chunked = _request.get_header("Transfer-Encoding");
                            if (chunked != null && chunked.equals("chunked")) // 说明是分段传输
                            {
                                _chunked_body = true;
                                continue;  // 设置标记，然后立即继续读取
                            } else  // 必须二选一，那么HTTP/0.9 和 HTTP1.0 可能没法POST东西 , 如需支持那么由于无法获知长度结果无法预测
                            {
                                return RequestParserState.Error;
                            }
                        } else {
                            _wait_body_size = Integer.parseInt(content_length);
                        }
                    }
                }
                if(_wait_body_size  == 0) return parse_request_params();
                // 数据还不够
                if(_wait_body_size > this._body_buffer.remaining()) return RequestParserState.Continue;
                if(_chunked_body) //如果是chunk的就继续往后等待
                {
                    this._body_buffer.set_read_index(this._body_buffer.get_read_index() + _wait_body_size);
                    _wait_body_size = -1;
                }
            }
        }
        return RequestParserState.Continue;
    }

    /**
     * 读取一行数据，以\r\n为结束标记读取
     * @return
     */
    private String read_line(ReadableByteBuffer byteBuffer)
    {
        StringBuilder sb = new StringBuilder();
        byteBuffer.mark();  // 先记录，防止读取不全的时候没法恢复
        while (byteBuffer.hasRemaining())
        {
            byte b = byteBuffer.get();
            if(b == '\r' && byteBuffer.hasRemaining())
            {
                byte c = byteBuffer.get();
                if(c == '\n') return sb.toString();  // 遇到\r\n就返回
                sb.append(b);
                sb.append(c);
            }
        }
        byteBuffer.reset();
        return null;
    }

    private RequestParserState parse_request_params()
    {
        // 先把URL里面的参数记录下来
        String[] url_params = this._request.path.split("\\?");
        if(url_params.length > 1)
        {
            try {
                this._request.path = url_params[0];
                this.parse_url_params(url_params[1]);
            } catch (UnsupportedEncodingException e) {
                return RequestParserState.Error;
            }
        }
        if(_request.method.equals("POST"))
        {
            String content_type = _request.get_header("Content-Type");
            if(content_type.equals("application/x-www-form-urlencoded"))
            {
                // 读取POST body里面的参数列表
                int size = this._body_buffer.remaining();
                if(size > 0)
                {
                    byte[] bytes = new byte[size];
                    this._body_buffer.get(bytes,0, size);
                    String url_body = new String(bytes);
                    try
                    {
                        this.parse_url_params(url_body);
                    } catch (UnsupportedEncodingException e) {
                        return RequestParserState.Error;
                    }
                }
                return RequestParserState.Completed;
            } else if (content_type.startsWith("multipart/form-data"))
            {
                String[] parts = content_type.split(";");
                String boundary = null;
                for(String part : parts)
                {
                    if(part.startsWith("boundary="))
                    {
                        boundary = part.split("=")[1].trim();
                        break;
                    }
                }
                if(boundary == null) return RequestParserState.Error; // 没有分割符定义
                // TODO
            }else
            {
                // 其他格式body默认不解析
                return RequestParserState.Completed;
            }
        }else
        {
            return RequestParserState.Completed;
        }
        return RequestParserState.Error;
    }

    private void parse_url_params(String url) throws UnsupportedEncodingException
    {
        String[] pairs = url.split("&");
        for(String pair : pairs)
        {
            String[] key_value = pair.split("=");
            if(key_value.length != 2) continue;
            String key = URLDecoder.decode(key_value[0], "UTF-8");
            String value = URLDecoder.decode(key_value[1], "UTF-8");
            if(!this._request.params.containsKey(key))
            {
                this._request.params.put(key, new LinkedList<String>());
            }
            this._request.params.get(key).add(value);
        }
    }
}
