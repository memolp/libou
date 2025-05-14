package org.jeff.web.response;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class StreamingRangeResponse extends StreamingResponse implements FileRangeResponse
{
    private long start = -1;
    private long end = -1;
    private long send_size = 0;
    private boolean send_chunk = false;

    public StreamingRangeResponse()
    {

    }

    @Override
    public void set_range(long start, long end)
    {
        long size = this.file.length();
        if(this.start < 0 && this.end < 0)
        {
            this.set_header("Content-Range", String.format("bytes */%s", size));
            this.set_status(416);
            return;
        }else if(this.start >= 0 && this.start >= size)
        {
            this.set_header("Content-Range", String.format("bytes */%s", size));
            this.set_status(416);
            return;
        }else if(this.end >= 0 && this.end >= size)
        {
            this.set_header("Content-Range", String.format("bytes */%s", size));
            this.set_status(416);
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
        this.send_size = Math.min(this.end - this.start +1, this.buff_size);
        this.end = this.send_size + this.start - 1;
//        this.set_header("Content-Range", String.format("bytes %d-%d/%d", this.start, this.end, size));
//        this.set_header("Content-Length", String.format("%d", this.send_size));
        this.set_status(206);
    }

    public void onBeforeWriteHeader()
    {
        long size = this.file.length();
        this.set_header("Content-Type", "video/mp4");
        this.set_header("Content-Range", String.format("bytes %d-%d/%d", this.start, this.end, size));
        this.set_header("Content-Length", String.format("%d", this.send_size));
    }

    @Override
    public void set_range(String range)
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
        this.set_range(this.start, this.end);
    }

    @Override
    public ByteBuffer next_trunk()
    {
        if(send_chunk) return null;
        try
        {
            send_chunk = true;
            byte[] bytes = new byte[(int) this.send_size];
            RandomAccessFile rf = new RandomAccessFile(this.file, "r");
            rf.seek(this.start);
            int size = rf.read(bytes, 0, bytes.length);
            assert size == this.send_size;
            ByteBuffer chunk = ByteBuffer.wrap(bytes, 0, size);
            rf.close();
            System.out.printf("Range Send size:%d this:%s\r\n", this.send_size, this);
            return chunk;
        } catch (Exception e)
        {
            this.set_status(500).write(e.getMessage());
        }
        return null;
    }
}
