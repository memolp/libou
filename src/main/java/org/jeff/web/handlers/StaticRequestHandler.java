package org.jeff.web.handlers;

import org.jeff.web.HttpDateHelper;
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
    public Response head(Request request, Router router)
    {
        return this.doRequest(request, router, false);
    }

    @Override
    public Response get(Request request, Router router)
    {
        return this.doRequest(request, router, true);
    }

    private Response doRequest(Request request, Router router, boolean include_body)
    {
        File file = getFile(request, router);
        if(file == null)
        {
            return ResponseBuilder.build(404);
        }
        String request_range = request.get_header("Range");
        if(request_range != null && this.support_range)
        {
            FileRangeResponse response = ResponseBuilder.buildRange();
            response.set_file(file);
            response.set_range(request_range);
            response.only_header(!include_body);
            return response;
        }
        FileResponse response = ResponseBuilder.buildStream();
        response.set_file(file);
        response.only_header(!include_body);
        if(support_cache)
        {
            String if_modify_date = request.get_header("If-Modify-Date");
            String cache_control = request.get_header("Cache-Control");
            String file_date = HttpDateHelper.formatToRFC822(file.lastModified());
            if(cache_control != null && cache_control.equals("no-store"))
            {
                return response;
            }
            response.set_header("Last-Modify-Date", file_date);
            response.set_header("Cache-Control", String.format("max-age=%d", max_age));
            if(if_modify_date != null && if_modify_date.equals(file_date))
            {
                response.only_header(true);
                response.set_status(304);
                return response;
            }
        }
        response.set_header("Accept-Range", "bytes");
        return response;
    }

    private File getFile(Request request, Router router)
    {
        ResourceRouter resourceRouter = (ResourceRouter)router;
        String path = request.path.replace(resourceRouter.resPath, "");
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
