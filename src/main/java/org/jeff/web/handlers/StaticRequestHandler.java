package org.jeff.web.handlers;

import org.jeff.web.utils.HttpDateHelper;
import org.jeff.web.Request;
import org.jeff.web.response.*;
import org.jeff.web.router.ResourceRouter;
import org.jeff.web.router.Router;

import java.io.File;


public class StaticRequestHandler extends RequestHandler
{
    public boolean support_cache = true;
    public boolean support_range = true;
    public int max_age = 86400;

    @Override
    public void head(Request request, Response response)
    {
        this.doRequest(request, response);
    }

    @Override
    public void get(Request request, Response response)
    {
        this.doRequest(request, response);
    }

    private void doRequest(Request request, Response response)
    {
        File file = getFile(request);
        if(file == null)
        {
            response.set_status(404, "Not Found");
            return;
        }
        String request_range = request.get_header("Range");
        if(request_range != null && this.support_range)
        {
            response.write(new FileRangeWriter(file, request_range));
            return;
        }
        if(support_cache)
        {
            String if_modify_date = request.get_header("If-Modify-Date");
            String cache_control = request.get_header("Cache-Control");
            String file_date = HttpDateHelper.formatToRFC822(file.lastModified());
            if(cache_control != null && cache_control.equals("no-store"))
            {
                response.write(new FileWriter(file));
                return;
            }
            response.set_header("Last-Modify-Date", file_date);
            response.set_header("Cache-Control", String.format("max-age=%d", max_age));
            if(if_modify_date != null && if_modify_date.equals(file_date))
            {
                response.set_status(304);
                return;
            }
        }
        response.set_header("Accept-Range", "bytes");
        response.write(new FileWriter(file));
    }

    private File getFile(Request request)
    {
        ResourceRouter resourceRouter = (ResourceRouter)this.router;
        String path = request.path.replace(resourceRouter.routerPath, "");
        String filename;
        if(!path.startsWith("/"))
        {
            filename = String.format("%s/%s", resourceRouter.resLocalRoot, path);
        }else
        {
            filename = String.format("%s%s", resourceRouter.resLocalRoot, path);
        }
        File file = new File(filename);
        if(!file.exists())
        {
            return null;
        }
        if(!file.isFile())
        {
            return null;
        }
        if(!file.canRead())
        {
            return null;
        }
        return file;
    }
}
