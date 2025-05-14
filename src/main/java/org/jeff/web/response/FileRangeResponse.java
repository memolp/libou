package org.jeff.web.response;

public interface FileRangeResponse extends FileResponse
{
    public void set_range(long start, long end);
    public void set_range(String range);
}
