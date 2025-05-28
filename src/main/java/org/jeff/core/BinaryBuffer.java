package org.jeff.core;

import java.util.Arrays;

/**
 * 二进制Buffer，将读写分开，方便边读边写的业务场景
 */
public class BinaryBuffer implements ReadableByteBuffer
{
    private byte[] _buffer = null;

    private int _writeIndex =0;

    private int _readIndex = 0;
    private int _readMark = -1;

    public BinaryBuffer(int capacity)
    {
        this._buffer = new byte[capacity];
    }

    public BinaryBuffer(byte[] data)
    {
        this._buffer = Arrays.copyOf(data, data.length);
        this._writeIndex = data.length;
    }

    public BinaryBuffer(byte[] data, int offset, int length)
    {
        this._buffer = Arrays.copyOfRange(data, offset, length);
        this._writeIndex = length;
    }

    public void mark()
    {
        this._readMark = this._readIndex;
    }

    public void reset()
    {
        if(this._readMark == -1) throw new RuntimeException("not mark");
        this._readIndex = this._readMark;
    }

    public byte get()
    {
        this.checkReadable(1);
        return this._buffer[this._readIndex++];
    }

    public void get(byte[] data, int offset, int length)
    {
        this.checkReadable(length);
        System.arraycopy(this._buffer, this._readIndex, data, offset, length);
        this._readIndex += length;
    }

    public void put(byte b)
    {
        this.ensureCapacity(this._writeIndex + b);
        this._buffer[this._writeIndex++] = b;
    }

    public void put(byte[] data, int offset, int length)
    {
        this.ensureCapacity(this._writeIndex + length);
        System.arraycopy(data, offset, this._buffer, this._writeIndex, length);
        this._writeIndex += length;
    }

    public boolean hasRemaining()
    {
        return this._readIndex < this._writeIndex;
    }

    public int remaining()
    {
        if(this._readIndex <= this._writeIndex)
            return this._writeIndex - this._readIndex;
        return 0;
    }

    public void clear()
    {
        this._writeIndex = 0;
        this._readIndex = 0;
        this._readMark = -1;
    }

    public int get_readIndex()
    {
        return this._readIndex;
    }

    public int get_writeIndex()
    {
        return this._writeIndex;
    }

    public byte[] array() {
        return this._buffer;
    }

    private void checkReadable(int size)
    {
        if(this._readIndex + size > this._writeIndex) throw new RuntimeException("readable size is less than " + size);
    }

    private void ensureCapacity(int required)
    {
        if(required > this._buffer.length)
        {
            int new_capacity = Math.max(this._buffer.length * 2, required);
            this._buffer = Arrays.copyOf(this._buffer, new_capacity);
        }
    }
}
