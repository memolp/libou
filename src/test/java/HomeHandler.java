import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.Response;
import org.jeff.web.response.ResponseBuilder;
import org.jeff.web.router.Router;

public class HomeHandler extends RequestHandler
{
    public Response get(Request request, Router router)
    {
        Response response = ResponseBuilder.build(200);
        response.set_header("Content-Type", "text/html; charset=UTF-8");
        response.write("<html><head><title>AAA</title></head><script src='/static/1.js'></script><body><video class=\"video-ctrl\" controls=\"controls\" autoplay=\"autoplay\" sizew=\"1912\" width=\"824\">\n" +
                "                <source src=\"/static/A.mp4\" type=\"video/mp4\">\n" +
                "            </video></body></html>");
        return response;
    }
}