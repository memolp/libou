import org.jeff.web.Application;
import org.jeff.web.ws.WebSocketMessage;
import org.jeff.web.ws.WebSocketRouter;
import org.jeff.web.ws.WebSocketSession;


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
        app.router.add_route(new WebSocketRouter("/ws", new WebSocketMessage()
        {
            @Override
            public void onConnected(WebSocketSession session) {
                System.out.println("onConnected:" + session.SessionID);
            }

            @Override
            public void onDisconnected(WebSocketSession session) {
                System.out.println("onDisconnected:" + session.SessionID);
            }

            @Override
            public void onMessage(WebSocketSession session, int type, byte[] data) {
                System.out.println("onMessage:" + session.SessionID + " type:" + type);
            }
        }));
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
