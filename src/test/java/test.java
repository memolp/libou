import org.jeff.web.Application;


public class test
{
    public static void main(String[] args) throws Exception
    {
        System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径
        Application app = new Application();
        app.router.add_route("/", IndexHandler.class);
        app.router.add_route("/home", HomeHandler.class);
        app.router.add_static("/static", "src/test/static");
        app.router.add_static("/tc/assets/", "E:\\G-FLite\\test\\code\\TestCenterWeb\\WebContent\\assets");
        app.router.add_regex("/oc/.+", FileDownloadHandler.class);
        for(int i = 0; i < args.length; i++)
        {
            if(args[i].contains("close"))
            {
                app.context.keepAlive = false;
            }
        }
        System.out.println("starting... keepAlive:" + app.context.keepAlive);
        app.start("0.0.0.0", 8080);
    }
}
