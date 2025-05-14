package org.jeff.web.response;

import java.nio.ByteBuffer;


public class StreamingChunkResponse extends StreamingResponse implements FileChunkResponse
{
    private boolean send_last_chunk = false;
    public StreamingChunkResponse()
    {

    }

    @Override
    public void onBeforeWriteHeader()
    {
        super.onBeforeWriteHeader();
        this.headers.remove("Content-Length");
        this.set_header("Transfer-Encoding", "chunked");
    }

    @Override
    public ByteBuffer next_trunk()
    {
        if(send_last_chunk) return null;

        ByteBuffer chunk = super.next_trunk();
        if(chunk == null)
        {
            send_last_chunk = true;
            return ByteBuffer.wrap(String.format("%X\r\n", 0).getBytes());
        }

        byte[] chunk_size = String.format("%X\r\n", chunk.remaining()).getBytes();
        ByteBuffer send_chunk = ByteBuffer.allocate(chunk_size.length + chunk.remaining());
        send_chunk.put(chunk_size);
        send_chunk.put(chunk);
        send_chunk.flip();
        return send_chunk;
    }
}
