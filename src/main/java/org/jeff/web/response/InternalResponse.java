package org.jeff.web.response;

import org.jeff.web.HttpContext;
import org.jeff.web.utils.HttpDateHelper;

import java.nio.ByteBuffer;

public class InternalResponse extends Response
{
    public InternalResponse()
    {
    }

    public String build_header(HttpContext context)
    {
        this.onBeforeWriteHeader(context);
        StringBuilder header = new StringBuilder();
//        header.append(this.version).append(" ").append(this.responseStatus.code()).append(" ").append(this.responseStatus.reason()).append("\r\n");
        header.append(String.format("%s %d %s\r\n", this.version, this.responseStatus.code(), this.responseStatus.reason()));
        for(String key : this.headers.keySet())
        {
            String value = this.headers.get(key);
            header.append(String.format("%s: %s\r\n", key, value));
//            header.append(key).append(": ").append(value).append("\r\n");
        }
        if(!this.headers.containsKey("Content-Type"))
        {
            header.append("Content-Type: text/plain; charset=UTF-8\r\n");
        }
        for(String key : this.cookies.keySet())
        {
            header.append(String.format("Set-Cookie: %s\r\n", this.cookies.get(key)));
//            header.append("Set-Cookie: ").append(this.cookies.get(key)).append("\r\n");
        }
        header.append("\r\n");
        return header.toString();
    }

    public void onBeforeWriteHeader(HttpContext context)
    {
        if(this.mBodyWriter == null)
        {
            this.set_header("Content-Length", "0");
            return;
        }
        this.mBodyWriter.onBeforeWriteHeader(this, context);
    }

    public ByteBuffer nextChunk()
    {
        if(this.mBodyWriter == null) return null;
        return this.mBodyWriter.nextChunk();
    }

    public boolean hasNext()
    {
        if(this.mBodyWriter == null) return false;
        return this.mBodyWriter.hasNext();
    }
}
