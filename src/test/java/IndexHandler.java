import org.jeff.web.handlers.RequestHandler;

public class IndexHandler extends RequestHandler
{
    @Override
    public void get()
    {
        this.response.write("Hello, World!");
    }
}