package org.jeff.web.multipart;

import org.jeff.core.DynamicBuffer;

public class FileField
{
    public String filename;
    public String contentType;
    public int contentLength;
    private DynamicBuffer _buffer;
    private int _pos;

    public FileField(DynamicBuffer buffer,String filename, String contentType, int pos, int contentLength)
    {
        this.filename = filename;
        this.contentType = contentType;
        this._buffer = buffer;
        this._pos = pos;
        this.contentLength = contentLength;
    }

    @Override
    public String toString()
    {
        return String.format("filename=%s; contentType=%s; length=%d", this.filename, this.contentType, this.contentLength);
    }
}
