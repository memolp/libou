import org.jeff.web.handlers.RequestHandler;

public class HomeHandler extends RequestHandler
{
    @Override
    public void get() {
        this.response.write("Home Hello World");
    }
}