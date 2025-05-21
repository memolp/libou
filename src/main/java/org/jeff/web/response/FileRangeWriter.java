package org.jeff.web.response;

import org.jeff.web.HttpContext;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * 一般用于视频分段传输
 */
public class FileRangeWriter extends FileWriter
{
    private long start = -1, end = -1;
    private long send_size;
    private boolean send_chunk = false;

    public FileRangeWriter(File file, String range)
    {
        super(file);
        this.init_range(range);
    }

    public FileRangeWriter(File file, String range, String mineType)
    {
        super(file, mineType);
        this.init_range(range);
    }

    public FileRangeWriter(File file, String range, String mineType, String filename)
    {
        super(file, mineType, filename);
        this.init_range(range);
    }

    private void init_range(String range)
    {
        String[] pairs = range.split("=", 2);
        if(pairs.length != 2) throw new IllegalArgumentException("Invalid range");
        if(!pairs[0].equals("bytes")) throw new IllegalArgumentException("Invalid range");

        range = pairs[1];
        if(range.endsWith("-"))  // bytes=500-
        {
            this.start = Long.parseLong(range.split("-")[0]);
        }else if(range.startsWith("-")) // bytes=-500
        {
            this.end = Long.parseLong(range.split("-")[0]);
        }else
        {
            String[] ranges = range.split("-", 2);
            if(ranges.length != 2) throw new IllegalArgumentException("Invalid range");
            this.start = Long.parseLong(ranges[0]);
            this.end = Long.parseLong(ranges[1]);
        }
    }

    @Override
    public void onBeforeWriteHeader(Response response, HttpContext context)
    {
        this.mBuffSize = context.buffSize;
        long size = this.contentLength();
        if(this.start < 0 && this.end < 0)
        {
            response.set_header("Content-Range", String.format("bytes */%s", size));
            response.set_status(416);
            return;
        }else if(this.start >= 0 && this.start >= size)
        {
            response.set_header("Content-Range", String.format("bytes */%s", size));
            response.set_status(416);
            return;
        }else if(this.end >= 0 && this.end >= size)
        {
            response.set_header("Content-Range", String.format("bytes */%s", size));
            response.set_status(416);
            return;
        }
        if(this.start < 0)
        {
            this.start = size - end;
        }
        if(this.end < 0)
        {
            this.end = size - 1;
        }
        this.send_size = Math.min(this.end - this.start +1, this.mBuffSize);
        this.end = this.send_size + this.start - 1;
        response.set_header("Content-Range", String.format("bytes %d-%d/%d", this.start, this.end, size));
        response.set_header("Content-Type", this.mMimeType);
        response.set_header("Content-Length", String.format("%d", this.send_size));
        response.set_status(206);
        this.send_chunk = true;
    }

    @Override
    public long contentLength()
    {
        return super.contentLength();
    }

    @Override
    public boolean hasNext()
    {
        return false;
    }

    @Override
    public ByteBuffer nextChunk()
    {
        if(!send_chunk) return null;
        try
        {
            byte[] bytes = new byte[(int) this.send_size];
            RandomAccessFile rf = new RandomAccessFile(this.mFile, "r");
            rf.seek(this.start);
            int size = rf.read(bytes, 0, bytes.length);
            assert size == this.send_size;
            ByteBuffer chunk = ByteBuffer.wrap(bytes, 0, size);
            rf.close();
            return chunk;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
