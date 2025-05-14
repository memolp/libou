package org.jeff.web.response;

import java.io.File;

public class ResponseBuilder
{
    public static Response build()
    {
        return new BaseResponse();
    }

    public static Response build(int status)
    {
        return new BaseResponse(status);
    }

    public static FileResponse buildStream()
    {
        return new StreamingResponse();
    }

    public static FileRangeResponse buildRange()
    {
        return new StreamingRangeResponse();
    }

    public static FileChunkResponse buildChunk()
    {
        return new StreamingChunkResponse();
    }
}
