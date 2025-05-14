import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.Response;
import org.jeff.web.response.ResponseBuilder;
import org.jeff.web.router.Router;

public class IndexHandler extends RequestHandler
{
    @Override
    public Response get(Request request, Router router)
    {
        Response response = ResponseBuilder.build(200);
        response.write("Hello, World!");
        return response;
    }
}