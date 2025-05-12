package org.jeff.core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * 动态二进制Buffer，当写入的内容不超过max_buff_size的时候都是直接使用内存Buffer，否则采用文件buff
 */
public class DynamicBuffer implements Closeable, ReadableByteBuffer
{
    private byte[] _buffer = null;
    private File _file = null;
    private RandomAccessFile _fileBuffer = null;
    private int _max_buff_size = 1024*1024*10;
    private boolean _switch_to_file = false;

    private int _writeIndex =0;

    private int _readIndex = 0;
    private int _readMark = -1;

    public DynamicBuffer(int capacity)
    {
        this._buffer = new byte[capacity];
    }

    public DynamicBuffer(byte[] data)
    {
        this._buffer = Arrays.copyOf(data, data.length);
        this._writeIndex = data.length;
    }

    public DynamicBuffer(byte[] data, int offset, int length)
    {
        this._buffer = Arrays.copyOfRange(data, offset, length);
        this._writeIndex = length;
    }

    public void set_max_buff_size(int _max_buff_size)
    {
        this._max_buff_size = _max_buff_size;
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
        if(this._switch_to_file)
        {
            try {
                this._fileBuffer.seek(this._readIndex++);
                return this._fileBuffer.readByte();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else
        {
            this.checkReadable(1);
            return this._buffer[this._readIndex++];
        }
    }

    public void get(byte[] data, int offset, int length)
    {
        if(this._switch_to_file)
        {
            try
            {
                this._fileBuffer.seek(this._readIndex);
                this._fileBuffer.read(data, offset, length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            this.checkReadable(length);
            System.arraycopy(this._buffer, this._readIndex, data, offset, length);
        }
        this._readIndex += length;
    }

    public void set_read_index(int read_index)
    {
        this._readIndex = read_index;
    }

    public int get_read_index()
    {
        return this._readIndex;
    }

    public int get_write_index()
    {
        return this._writeIndex;
    }

    public void put(byte b)
    {
        this.check_switch_file(1);
        if(this._switch_to_file)
        {
            try {
                this._fileBuffer.seek(this._writeIndex);
                this._fileBuffer.write(b);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else
        {
            this.ensureCapacity(this._writeIndex + b);
            this._buffer[this._writeIndex] = b;
        }
        this._writeIndex++;
    }

    public void put(byte[] data, int offset, int length)
    {
        this.check_switch_file(length);
        if(this._switch_to_file)
        {
            try
            {
                this._fileBuffer.seek(this._writeIndex);
                this._fileBuffer.write(data, offset, length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else
        {
            this.ensureCapacity(this._writeIndex + length);
            System.arraycopy(data, offset, this._buffer, this._writeIndex, length);
        }
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

    private void checkReadable(int size)
    {
        if(this._readIndex + size > this._writeIndex) throw new RuntimeException("readable size is less than " + size);
    }
    private void check_switch_file(int size)
    {
        if(!this._switch_to_file && (this._writeIndex + size > this._max_buff_size))
        {
            try
            {
                this._file = File.createTempFile("libou_http_request_" + this.hashCode(), ".tmp");
                this._file.deleteOnExit();
                this._fileBuffer = new RandomAccessFile(this._file, "rw");
                // 将当前已写入的数据放入文件
                this._fileBuffer.write(this._buffer, 0, this._writeIndex);
                this._buffer = null;
                this._switch_to_file = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void ensureCapacity(int required)
    {
        if(required > this._buffer.length)
        {
            int new_capacity = Math.max(this._buffer.length * 2, required);
            this._buffer = Arrays.copyOf(this._buffer, new_capacity);
        }
    }

    @Override
    public void close() throws IOException
    {
        if(this._fileBuffer != null) this._fileBuffer.close();
        if(this._file != null) this._file.deleteOnExit();
    }
}
