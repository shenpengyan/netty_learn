package com.shenpengyan.netty_learn.echo;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoServerHandler extends ChannelHandlerAdapter {

    private int counter;

    private EchoServer echoServer;
    
    private Channel inboundChannel;
    
    public EchoServerHandler(EchoServer echoServer) {
        this.echoServer = echoServer;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel " + ctx.channel().remoteAddress().toString() + "open");
        inboundChannel = ctx.channel();
        echoServer.setIPPortMap(ctx.channel().remoteAddress().toString(), EchoServerHandler.this);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("EchoServerHandler " + this.hashCode() );
        String body = (String) msg;
        System.out.println("This is " + ++counter + " times receive client : [" + body + " ]");
        InetSocketAddress isa = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("host and port " + isa.getHostName() + ":" + isa.getPort());
        body += "$_";
        ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel " + ctx.channel().remoteAddress().toString() + "close");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
    public void sendSomething() {
        String body = "auto msg";
        body += "$_";
        ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
        inboundChannel.writeAndFlush(resp);
    }

}
