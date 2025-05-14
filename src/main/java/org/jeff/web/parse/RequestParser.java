package org.jeff.web.parse;

import org.jeff.core.BinaryBuffer;
import org.jeff.core.DynamicBuffer;
import org.jeff.core.ReadableByteBuffer;
import org.jeff.web.Request;
import org.jeff.web.multipart.FileField;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;

public class RequestParser
{
    enum RequestParserFSM
    {
        METHOD,
        HEADER,
        BODY,
    }

    public Request request = new Request();

    private BinaryBuffer _cache_buffer = new BinaryBuffer(1024);
    private RequestParserFSM _state = RequestParserFSM.METHOD;
    private DynamicBuffer _body_buffer = null;
    private int _wait_body_size = -1;
    private boolean _chunked_body = false;

    public RequestParser()
    {
    }

    public RequestParserState parser(ByteBuffer byteBuffer) throws RequestParseException
    {
        if(_state != RequestParserFSM.BODY)
            _cache_buffer.put(byteBuffer.array(), 0, byteBuffer.remaining());
        else
            _body_buffer.put(byteBuffer.array(), 0, byteBuffer.remaining());
        return this.http_fsm_parser();
    }

    private RequestParserState http_fsm_parser() throws RequestParseException
    {
        // 解析HTTP的请求内容
        while(_cache_buffer.hasRemaining())
        {
            if (_state == RequestParserFSM.METHOD)  // 解析请求方法
            {
                String line = read_line(this._cache_buffer);
                if (line == null) return RequestParserState.Continue;
                String[] args = line.split(" ");
                if  (args.length < 2)
                {
                    throw new RequestParseException("解析HTTP方法异常, 原始:%s", line);
                }
                request.method = args[0].trim().toUpperCase();
                request.path = args[1].trim();
                if (args.length == 3) {
                    request.version = args[2].trim();
                } else {
                    request.version = "HTTP/0.9";
                }
                _state = RequestParserFSM.HEADER;
            } else if (_state == RequestParserFSM.HEADER)   // 读取头数据
            {
                String line = read_line(this._cache_buffer);
                if(line == null) return RequestParserState.Continue;
                if(line.isEmpty())
                {
                    if(!request.method.equals("POST"))  // 只有POST方法存在body
                    {
                        return parse_request_params();
                    }
                    _state = RequestParserFSM.BODY;
                    continue;
                }
                String[] args = line.split(":", 2);
                if(args.length <2)
                {
                    throw new RequestParseException("解析HTTP头异常, 原始:%s", line);
                }
                request.headers.put(args[0].trim().toUpperCase(), args[1].trim());
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
                        String content_length = request.get_header("Content-Length");
                        if (content_length == null)  // 没有指定长度
                        {
                            String chunked = request.get_header("Transfer-Encoding");
                            if (chunked != null && chunked.equals("chunked")) // 说明是分段传输
                            {
                                _chunked_body = true;
                                continue;  // 设置标记，然后立即继续读取
                            } else  // 必须二选一，那么HTTP/0.9 和 HTTP1.0 可能没法POST东西 , 如需支持那么由于无法获知长度结果无法预测
                            {
                                throw new RequestParseException("当前服务器版本不支持不提供长度的请求");
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
        ByteArrayOutputStream sb = new ByteArrayOutputStream();
        byteBuffer.mark();  // 先记录，防止读取不全的时候没法恢复
        while (byteBuffer.hasRemaining())
        {
            byte b = byteBuffer.get();
            if(b == '\r')
            {
                if(byteBuffer.hasRemaining())
                {
                    byte c = byteBuffer.get();
                    if (c == '\n') return sb.toString();  // 遇到\r\n就返回
                    sb.write(b);
                    sb.write(c);
                }else
                {
                    break;
                }
            }else
                sb.write(b);
        }
        byteBuffer.reset();
        return null;
    }

    private RequestParserState parse_request_params() throws RequestParseException
    {
        // 先把URL里面的参数记录下来
        String[] url_params = this.request.path.split("\\?", 2);
        if(url_params.length > 1)
        {
            try {
                this.request.path = url_params[0];
                this.parse_url_params(url_params[1]);
            } catch (UnsupportedEncodingException e) {
                throw new RequestParseException("解析URL里面的参数异常，原始:%s", url_params[1]);
            }
        }
        if(request.method.equals("POST"))
        {
            String content_type = request.get_header("Content-Type");
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
                        throw new RequestParseException("解析Body里面的参数错误，原始:%s", url_body);
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
                if(boundary == null)
                    throw new RequestParseException("multipart/form-data格式请求没有携带boundary");
                this.multipart_parser(boundary);
            }else
            {
                // 其他格式body默认不解析
                return RequestParserState.Completed;
            }
        }else
        {
            return RequestParserState.Completed;
        }
        throw new RequestParseException("未知的解析参数错误");
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
            this.put_request_params(key, value);
        }
    }

    private void put_request_params(String key, Object value)
    {
        if(!this.request.params.containsKey(key))
        {
            this.request.params.put(key, new LinkedList<Object>());
        }
        this.request.params.get(key).add(value);
    }

    private String _boundary;
    private String _end_boundary;
    private void multipart_parser(String boundary)
    {
        this._boundary = "--" + boundary;
        this._end_boundary = "--" + boundary + "--";
        byte[] _bytes_boundary = this._boundary.getBytes();
        while (true)
        {
            if(!this.parse_field(_bytes_boundary)) break;
        }
    }

    private boolean parse_field(byte[] _bytes_boundary)
    {
        String line = this.read_line(this._body_buffer);
        if(line == null || !line.startsWith(this._boundary)) return false;  // 格式异常
        if(line.equals(this._end_boundary)) return false;  // 已读取完成
        HashMap<String, String> headers = this.parse_headers();
        int start_pos = this._body_buffer.remaining();  // 头读取完成后的位置
        int end_pos = this.read_boundary_pos(_bytes_boundary);  // 读取到下一个开始的位置
        if(end_pos == -1) return false;  // 异常
        if(!headers.containsKey("Content-Disposition".toLowerCase())) return false; // 异常
        String disposition = headers.get("Content-Disposition".toLowerCase());
        if(!disposition.startsWith("form-data")) return false; // 异常
        if(!headers.containsKey("name")) return false; // 异常
        String name = headers.get("name");
        int size = end_pos - start_pos;
        if(!headers.containsKey("filename"))  // 普通数据
        {
            byte[] bytes = new byte[size];
            this._body_buffer.get(bytes, 0, size);
            String value = new String(bytes,0, size);
            this.put_request_params(name, value.trim());
            this._body_buffer.set_read_index(end_pos);
            return true;
        }
        String filename = headers.get("filename".toLowerCase());
        String contentType = headers.get("Content-Type".toLowerCase());
        FileField file = new FileField(this._body_buffer, filename, contentType , start_pos, size);
        this.put_request_params(name, file);
        this._body_buffer.set_read_index(end_pos);
        return true;
    }

    private HashMap<String, String> parse_headers()
    {
        HashMap<String, String> headers = new HashMap<>();
        while (true)
        {
            String line = read_line(this._body_buffer);
            if(line == null || line.isEmpty()) break;  // 头结束了
            String[] pairs = line.split(":");
            if(pairs.length != 2) continue;; // 格式不对
            String key = pairs[0].trim();
            String value = pairs[1].trim();
            String[] sub_pairs = value.split(";");
            if(sub_pairs.length == 1)
            {
                headers.put(key.toLowerCase(), value.trim());
                continue;
            }
            for(String pair : pairs)
            {
                String[] kv = pair.split("=");
                if(kv.length !=2) continue;
                headers.put(kv[0].trim().toLowerCase(), kv[1].trim());
            }
        }
        return headers;
    }

    /**
     * 查找des在src的位置
     * @param src
     * @param des
     * @return
     */
    private int indexOf(byte[] src, byte[] des)
    {
        for (int i = 0; i <= src.length - des.length; i++)
        {
            boolean match = true;
            for (int j = 0; j < des.length; j++)
            {
                if (src[i + j] != des[j])
                {
                    match = false;
                    break;
                }
            }
            if (match)
            {
                return i;
            }
        }
        return -1;
    }

    private int read_boundary_pos(byte[] bytes_boundary)
    {
        int size = bytes_boundary.length;
        byte[] temp_buff = new byte[size];
        int temp_pos = 0;
        int temp_length = size * 2;
        while (true)
        {
            int temp_size = Math.min(temp_length, this._body_buffer.remaining());
            this._body_buffer.get(temp_buff, temp_pos, temp_size);
            int pos = indexOf(temp_buff, bytes_boundary);
            if (pos == -1)
            {
                if(temp_size - size < 1) return -1;
                System.arraycopy(temp_buff, 0, temp_buff, size, temp_size-size);
                temp_pos = size;
                temp_length = size;
                continue;
            }
            return this._body_buffer.remaining() - temp_pos + pos;
        }
    }
}
