package com.shenpengyan.netty_learn.test;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ZookeeperTest {

    public static void main(String[] args) throws Exception {

        String connectString = "127.0.0.1:8481";
        String nameSpace = "xp";
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString(connectString).namespace(nameSpace)
                .retryPolicy(new RetryNTimes(3, 30000)).connectionTimeoutMs(30000).build();

        curator.start();
        String infoPath = "info_pms";
        List<String> paths = curator.getChildren().forPath(infoPath);
        System.out.println(paths.toString());
        
        for (String chPath : paths) {
            String fullPath = "/" + infoPath + "/" + chPath;

            ZData data = new ZData();
            data.path = fullPath;
            data.data = curator.getData().forPath(fullPath);

            Integer svId = Integer.parseInt(ZKPaths.getNodeFromPath(fullPath));
            System.out.println(svId);
        }

    }

    private static class ZData {
        String path;
        byte[] data;
    }
    

    
}
