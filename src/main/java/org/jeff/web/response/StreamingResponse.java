package org.jeff.web.response;


import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StreamingResponse extends BaseResponse implements FileResponse
{
    protected int buff_size = 1024*1024*5;
    protected File file = null;
    protected String mineType = "application/octet-stream";
    protected String filename = "";

    private long _pos = 0;
    private RandomAccessFile _reader = null;
    private byte[] _buff = null;

    public StreamingResponse()
    {

    }

    public FileResponse set_file(File file)
    {
        this.file = file;
        this.filename = file.getName();
        return this;
    }

    public FileResponse set_file(File file, String mineType)
    {
        this.file = file;
        this.mineType = mineType;
        this.filename = file.getName();
        return this;
    }

    public FileResponse set_file(File file, String mineType, String filename)
    {
        this.file = file;
        this.mineType = mineType;
        this.filename = filename;
        return this;
    }

    //@Override
   /* public String build_header()
    {
        this.onBeforeWriteHeader();
        StringBuilder header = new StringBuilder();
        header.append(String.format("%s %s\r\n", this.version, this.responseStatus));
        for(String key : this.headers.keySet())
        {
            String value = this.headers.get(key);
            header.append(String.format("%s: %s\r\n", key, value));
        }
        if(!this.headers.containsKey("Connection"))
        {
            header.append("Connection: close\r\n");
        }
        if(!this.headers.containsKey("Content-Type"))
        {
            header.append("Content-Type: text/plain; charset=UTF-8\r\n");
        }
        if(!this.headers.containsKey("Date"))
        {
            header.append(String.format("Date: %s\r\n",new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH).format(new Date())));
        }
        header.append("\r\n");
        return header.toString();
    }*/

    public ByteBuffer next_trunk()
    {
        try
        {
            if (this._reader == null) this._reader = new RandomAccessFile(this.file, "r");
            if (this._buff == null) this._buff = new byte[this.buff_size];

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

    public void onBeforeWriteHeader()
    {
        this.set_header("Content-Type", this.mineType);
        this.set_header("Content-Length", String.format("%d", this.file.length()));
        this.set_header("Content-Disposition", String.format("attachment; filename=%s", this.filename));
    }
}
