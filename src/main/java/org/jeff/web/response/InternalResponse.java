package org.jeff.web.response;

import java.nio.ByteBuffer;

public interface InternalResponse
{
    String build_header();
    ByteBuffer next_trunk();
    boolean include_body();
    void onBeforeWriteHeader();
    boolean keepAlive();
}
