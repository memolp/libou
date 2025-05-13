package org.jeff.web;

import java.io.File;
import java.nio.ByteBuffer;

public class FileResponse extends Response
{
    public String path;

    public void write(ByteBuffer bytes)
    {
        File file = new File(path);
        if(file.exists())
        {
            if(file.isFile())
            {
                super.write(file.getPath());
            }
            else
            {
                super.write("404");
            }
        }
    }
}
