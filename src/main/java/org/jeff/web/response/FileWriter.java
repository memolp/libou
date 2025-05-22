package org.jeff.web.response;

import org.jeff.web.HttpContext;
import org.jeff.web.multipart.FileMineType;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class FileWriter implements IBodyWriter
{
    protected File mFile;
    protected String mMimeType;
    protected String mFilename;
    protected int mBuffSize =1024*1024*5;

    public FileWriter(File file)
    {
        this.mFile = file;
        this.mFilename = file.getName();
        this.mMimeType = FileMineType.from(this.mFilename);
    }

    public FileWriter(File file, String mineType)
    {
        this.mFile = file;
        this.mFilename = file.getName();
        this.mMimeType = mineType;
    }

    public FileWriter(File file, String mineType, String filename)
    {
        this.mFile = file;
        this.mMimeType = mineType;
        this.mFilename = filename;
    }

    @Override
    public void write(String content) throws IOException
    {
        throw new RuntimeException("FileWriter.write(String) not supported");
    }

    @Override
    public void onBeforeWriteHeader(Response response, HttpContext context)
    {
        this.mBuffSize = context.fileBuffSize;
        response.set_header("Content-Length", String.format("%d", this.contentLength()));
        response.set_header("Content-Type", this.mMimeType);
        response.set_header("Content-Disposition", String.format("attachment; filename=%s", this.mFilename));
    }

    @Override
    public long contentLength()
    {
        return this.mFile.length();
    }

    @Override
    public boolean hasNext()
    {
        return true;
    }

    private RandomAccessFile _reader = null;
    private byte[] _buff = null;
    private long _pos = 0;
    @Override
    public ByteBuffer nextChunk()
    {
        try
        {
            if (this._reader == null) this._reader = new RandomAccessFile(this.mFile, "r");
            if (this._buff == null) this._buff = new byte[this.mBuffSize];

            this._reader.seek(this._pos);
            int size = this._reader.read(this._buff, 0, this._buff.length);
            if (size <= 0) return null;  // 已经读完
            this._pos += size;
            return ByteBuffer.wrap(this._buff, 0, size);
        }catch (Exception e)
        {
            return null;
        }
    }
}
