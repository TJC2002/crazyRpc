package top.crazy.server;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.server
 * @Author: crazyPang
 * @CreateTime: 2023-05-18  19:41
 * @Description: TODO
 */

@Component
public class HttpServer {


    @Autowired
    MyDispatcherServlet myDispatcherServlet;
    public void start(String hostname,Integer port){
        //读取用户
        System.out.println("开启tomcat服务器 hostname"+hostname+" port"+port);
        Tomcat tomcat = new Tomcat();
        Server server = tomcat.getServer();

        Service service = server.findService("Tomcat");
        Connector connector = new Connector();

        connector.setPort(port);

        Engine engine = new StandardEngine();

        engine.setDefaultHost(hostname);

        Host host = new StandardHost();

        host.setName(hostname);

        String contextPath = "";

        Context context = new StandardContext();
        context.setPath(contextPath);

        context.addLifecycleListener(new Tomcat.FixContextListener());

        host.addChild(context);

        engine.addChild(host);

        service.setContainer(engine);

        service.addConnector(connector);

        tomcat.addServlet(contextPath,"dispatcher",myDispatcherServlet);

        context.addServletMappingDecoded("/*","dispatcher");

        try {
            tomcat.start();
            System.out.println("tomcat 启动成功");
            tomcat.getServer().await();
        }catch (LifecycleException lifecycleException){
            lifecycleException.printStackTrace();
        }



    }
}
