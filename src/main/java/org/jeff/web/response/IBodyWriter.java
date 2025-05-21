package org.jeff.web.response;

import org.jeff.web.HttpContext;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface IBodyWriter
{
    void write(String content) throws IOException;
    void onBeforeWriteHeader(Response response, HttpContext context);
    long contentLength();
    ByteBuffer nextChunk();
    boolean hasNext();
}
