package org.jeff.web.response;

import org.jeff.web.server.Session;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class WriteBodyHandler implements CompletionHandler<Integer, ByteBuffer>
{
    private final InternalResponse response;
    private final Session session;
    public WriteBodyHandler(Session session, InternalResponse response)
    {
        this.response = response;
        this.session = session;
    }

    public void doSendBody()
    {
        this.onWriteBodyCompleted(true);
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment)
    {
        if(attachment.hasRemaining())
        {
            this.session.write(attachment, attachment, this);
            return;
        }
        this.onWriteBodyCompleted(this.response.hasNext());
    }

    private void onWriteBodyCompleted(boolean hasNext)
    {
        if(hasNext)
        {
            ByteBuffer chunked = this.response.nextChunk();
            if (chunked != null) {
                this.session.write(chunked, chunked, this);
                return;
            }
        }
        this.session.onResponseCompleted(this.response);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment)
    {
        this.session.onResponseFailed(exc);
    }
}
