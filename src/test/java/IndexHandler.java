import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.Response;

public class IndexHandler extends RequestHandler
{
    @Override
    public void get(Request request, Response response)
    {
        response.write("Hello, World!");
    }
}