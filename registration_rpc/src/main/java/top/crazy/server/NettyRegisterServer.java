package top.crazy.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.crazy.handler.NettyRegisterHandler;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.server
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  21:19
 * @Description: TODO
 */

@Component
public class NettyRegisterServer implements InitializingBean {

    @Autowired
    NettyRegisterHandler nettyRegisterHandler;

    @Value("${rpc.register.port}")
    public Integer port;

    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //添加http编码解码器
                        socketChannel.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(nettyRegisterHandler);
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Register注册中心启动成功 端口："+port);
//            future.channel().closeFuture().sync();
//            boss.shutdownGracefully();
//            worker.shutdownGracefully();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}