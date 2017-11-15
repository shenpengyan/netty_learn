package com.shenpengyan.netty_learn.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountdownLatchTest {

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        final CountDownLatch cdOrder = new CountDownLatch(1);

        final CountDownLatch cdAnswer = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        System.out.println("线程" + Thread.currentThread().getName() + "正准备接受命令");
                        cdOrder.await();
                        System.out.println("线程 " + Thread.currentThread().getName() + "已接受命令");
//                        Thread.sleep((long) (Math.random() * 10000));
                        Thread.sleep((long) (10000));
                        System.out.println("线程" + Thread.currentThread().getName() + "回应命令处理结果");
                        cdAnswer.countDown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
            service.execute(runnable);
        }

        try {
            System.out.println("线程" + Thread.currentThread().getName() + "即将发布命令");
            cdOrder.countDown();
            System.out.println("线程" + Thread.currentThread().getName() + "已发送命令，正在等待结果");
            if(cdAnswer.await(3, TimeUnit.SECONDS)) {
                System.out.println("线程" + Thread.currentThread().getName() + "在3秒内已收到所有响应的结果");                
            }
            else{
                System.out.println("线程" + Thread.currentThread().getName() + "没有在3秒内收到所有响应的结果");
            }
            System.out.println("您随意，我不等了!");

        } catch (Exception e) {
            e.printStackTrace();
        }
        service.shutdown();
    }

}
