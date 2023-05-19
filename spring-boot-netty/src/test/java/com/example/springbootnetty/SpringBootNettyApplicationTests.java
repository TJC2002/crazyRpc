package com.example.springbootnetty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.crazy.server.NettyRegisterServer;

@SpringBootTest
class SpringBootNettyApplicationTests {

    @Test
    void contextLoads() {
        NettyRegisterServer nettyRegisterServer = new NettyRegisterServer();
        nettyRegisterServer.start();
    }

}
