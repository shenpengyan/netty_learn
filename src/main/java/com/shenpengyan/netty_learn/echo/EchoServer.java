package com.shenpengyan.netty_learn.echo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

    private Map<String, EchoServerHandler> ipPortMap = new ConcurrentHashMap<String, EchoServerHandler>();

    public void bind(int port) {
        autoEvent();
        // 配置服务端的 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChildChannelHandler());
        try {
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void autoEvent() {
        Thread t = new Thread(new Runnable() {
            
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    for (Map.Entry<String, EchoServerHandler> entry : ipPortMap.entrySet() ) {
                        System.out.println("now is " + entry.getKey());
                        entry.getValue().sendSomething();
                    }
                }
                
            }
        });
        t.start();
    }
    
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
            ch.pipeline().addLast(new StringDecoder());
            ch.pipeline().addLast(new EchoServerHandler(EchoServer.this));
        }

    }

    public void setIPPortMap(String key, EchoServerHandler esHandler) {
        ipPortMap.put(key, esHandler);
    }

    public Map<String, EchoServerHandler> getIPPortMap() {
        return ipPortMap;
    }
     
    public static void main(String[] args) {
        int port = 8082;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        EchoServer echoServer = new EchoServer();
        echoServer.bind(port);
        
        
    }

}
