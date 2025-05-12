package org.jeff.core;

public interface ReadableByteBuffer
{
    public byte get();
    public void get(byte[] data, int offset, int length);
    public void mark();
    public void reset();
    public boolean hasRemaining();
    public int remaining();
}
