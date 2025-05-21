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
        header.append(String.format("%s %s\r\n", this.version, this.responseStatus));
        for(String key : this.headers.keySet())
        {
            String value = this.headers.get(key);
            header.append(String.format("%s: %s\r\n", key, value));
        }
        if(!this.headers.containsKey("Connection"))
        {
            if(this.close_connection)
                header.append("Connection: close\r\n");
            else
                header.append("Connection: keep-alive\r\n");
        }
        if(!this.headers.containsKey("Content-Type"))
        {
            header.append("Content-Type: text/plain; charset=UTF-8\r\n");
        }
        if(!this.headers.containsKey("Date"))
        {
            header.append(String.format("Date: %s\r\n", HttpDateHelper.formatToRFC822()));
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

    public boolean keepAlive()
    {
        return !this.close_connection;
    }

}
