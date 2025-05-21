package org.jeff.web.response;

import org.jeff.web.HttpContext;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * 针对大文件、无法知道最终大小的内容发送
 */
public class FileChunkWriter extends FileWriter
{
    private boolean has_next = true;
    public FileChunkWriter(File file)
    {
        super(file);
    }

    public FileChunkWriter(File file, String mineType)
    {
        super(file, mineType);
    }

    public FileChunkWriter(File file, String mineType, String filename)
    {
        super(file, mineType, filename);
    }

    @Override
    public boolean hasNext()
    {
        return has_next;
    }

    @Override
    public void onBeforeWriteHeader(Response response, HttpContext context)
    {
        this.mBuffSize = context.buffSize;
        response.set_header("Transfer-Encoding", "chunked");
        response.set_header("Content-Type", this.mMimeType);
        response.set_header("Content-Disposition", String.format("attachment; filename=%s", this.mFilename));
    }
    private final byte[] LR = "\r\n".getBytes();
    @Override
    public ByteBuffer nextChunk()
    {
        ByteBuffer chunk = super.nextChunk();
        if(chunk == null)
        {
            has_next = false;
            return ByteBuffer.wrap(String.format("%X\r\n\r\n", 0).getBytes());
        }
        byte[] chunk_size = String.format("%X\r\n", chunk.remaining()).getBytes();
        ByteBuffer send_chunk = ByteBuffer.allocate(chunk_size.length + chunk.remaining() + LR.length);
        send_chunk.put(chunk_size);
        send_chunk.put(chunk);
        send_chunk.put(LR);
        send_chunk.flip();
        return send_chunk;
    }
}
