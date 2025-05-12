package org.jeff.web.http;

import org.jeff.core.DynamicBuffer;

import java.util.ArrayList;
import java.util.List;

public class MultipartParser
{
    private DynamicBuffer _buffer;
    private byte[] _boundary;
    private byte[] _end_boundary;
    public MultipartParser(DynamicBuffer buffer, String boundary)
    {
        this._buffer = buffer;
        this._boundary = ("--" + boundary).getBytes();
        this._end_boundary = ("--" + boundary + "--").getBytes();
    }

    public List<Field> parser()
    {
        List<Field> fields = new ArrayList<>();

        return fields;
    }
    /**
     * 查找des在src的位置
     * @param src
     * @param des
     * @return
     */
    private int indexOf(byte[] src, byte[] des)
    {
        for (int i = 0; i <= src.length - des.length; i++)
        {
            boolean match = true;
            for (int j = 0; j < des.length; j++)
            {
                if (src[i + j] != des[j])
                {
                    match = false;
                    break;
                }
            }
            if (match)
            {
                return i;
            }
        }
        return -1;
    }

    private int read_boundary_pos() {
        int size = this._boundary.length * 2;
        byte[] temp_buff = new byte[size];
        while (true) {
            int temp_size = Math.min(size, this._buffer.remaining());
            this._buffer.get(temp_buff, 0, temp_size);
            int pos = indexOf(temp_buff, this._boundary);
            if (pos == -1)
            {
                //temp_buff = temp_buff
                continue;
            }
            if (temp_size < size) {
                break;
            }
        }
        return -1;
    }
}
