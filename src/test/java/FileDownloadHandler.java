import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.FileChunkWriter;
import org.jeff.web.response.Response;

import java.io.File;

public class FileDownloadHandler extends RequestHandler
{
    @Override
    public void get(Request request, Response response)
    {
        File file = new File("E:\\JeffProject\\JavaProjects\\libou\\src\\test\\static\\Photoshop 2024 v25.0.0.37 Win.7z");
        response.write(new FileChunkWriter(file));
    }
}
