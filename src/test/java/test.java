import org.jeff.web.Application;

import java.io.IOException;

public class test
{
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Application app = new Application();
        app.start_server("localhost", 8080);
        //System.in.read();
        Thread.currentThread().join();
    }
}
