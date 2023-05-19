package top.crazy.rpcClient;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.rpcClient
 * @Author: crazyPang
 * @CreateTime: 2023-05-19  13:31
 * @Description: TODO
 */
@Component
public class BootRunner implements InitializingBean {
    @Autowired(required = false)
    NettyPrcClient nettyPrcClient;

    @Autowired(required = false)
    NettyRegistrationClient nettyRegistrationClient;

    @Value("${rpc.consumer.server.host}")
    String consumerHost;
    @Value("${rpc.consumer.server.port}")
    Integer consumerPort;

    @Value("${rpc.registration.host}")
    String registrationHost;
    @Value("${rpc.registration.port}")
    Integer registrationPort;

    @Value("${rpc.mode-registration}")
    boolean modeRegistration;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!modeRegistration){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nettyPrcClient.start(consumerHost,consumerPort);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nettyRegistrationClient.start(registrationHost,registrationPort);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


    }
}
