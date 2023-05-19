package top.crazy.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import top.crazy.server.HttpServer;
import top.crazy.server.NettyPrcServer;
import top.crazy.server.NettyRegisterClient;

import java.time.LocalDate;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-17  13:21
 * @Description: TODO
 */

@Component
public class BootRunner implements  InitializingBean  {
    @Autowired
    NettyRegisterClient nettyRegisterClient;

    @Autowired
    NettyServerHandler nettyServerHandler;

    @Autowired
    NettyPrcServer nettyPrcServer;

    @Value("${rpc.provider.http.host}")
    String httpHost;
    @Value("${rpc.provider.http.port}")
    Integer httpPort;

    @Autowired
    HttpServer httpServer;

    @Value("${rpc.mode-registration}")
    boolean modeRegistration;
//    ApplicationContext appContext;
    @Override
    public void afterPropertiesSet() throws Exception {
        if(!modeRegistration)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    nettyPrcServer.start();
                }
            }).start();
        else{
            //连接注册中心
            new Thread(new Runnable() {
                @Override
                public void run() {
                    nettyRegisterClient.start();
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpServer.start(httpHost,httpPort);
                }
            }).start();
        }

    }

//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.appContext=applicationContext;
//    }


}
