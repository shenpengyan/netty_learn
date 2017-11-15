package com.shenpengyan.netty_learn.test;

import java.net.InetSocketAddress;

public class TimeTest {

    public static void main(String[] args) {
        System.out.println(System.nanoTime());
        InetSocketAddress isa = new InetSocketAddress("127.0.0.2", 8080);
        System.out.println(isa.getHostName());
        System.out.println(isa.getAddress().toString());
        System.out.println(isa.getPort());
    }
    
}
