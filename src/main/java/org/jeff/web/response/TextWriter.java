package org.jeff.web.response;

import org.jeff.web.HttpContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TextWriter implements IBodyWriter
{
    private ByteArrayOutputStream content = new ByteArrayOutputStream();
    @Override
    public void write(String text) throws IOException
    {
        this.content.write(text.getBytes());
    }

    @Override
    public void onBeforeWriteHeader(Response response, HttpContext context)
    {
        response.set_header("Content-Length", String.format("%d", this.contentLength()));
    }

    @Override
    public long contentLength()
    {
        return this.content.size();
    }

    @Override
    public ByteBuffer nextChunk()
    {
        ByteBuffer buffer = ByteBuffer.wrap(this.content.toByteArray());
        try {
            this.content.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            this.content = null;
        }
        return buffer;
    }
    @Override
    public boolean hasNext()
    {
        return false;
    }
}
