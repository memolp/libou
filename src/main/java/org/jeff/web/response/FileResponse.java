package org.jeff.web.response;

import java.io.File;

public interface FileResponse extends Response
{
    FileResponse set_file(File file);
    FileResponse set_file(File file, String mineType);
    FileResponse set_file(File file, String mineType, String filename);
}
