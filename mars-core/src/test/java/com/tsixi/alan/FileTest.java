/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.alan;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * Created on 2017/3/16.
 *
 * @author Alan
 * @since 1.0
 */
public class FileTest {
    FileAlterationMonitor monitor = null;

    public FileTest(long interval) throws Exception {
        monitor = new FileAlterationMonitor(interval);
    }

    public void monitor(String path, FileAlterationListener listener) {
        FileAlterationObserver observer = new FileAlterationObserver(
                new File(path));
        monitor.addObserver(observer);
        observer.addListener(listener);
    }

    public void stop() throws Exception {
        monitor.stop();
    }

    public void start() throws Exception {
        monitor.start();
    }

    public static void main(String[] args) throws Exception {
        FileTest m = new FileTest(5000);
        m.monitor("E:\\log", new FileAlterationListener() {
            @Override
            public void onStart(FileAlterationObserver observer) {
//                System.out.println("onStart");
            }

            @Override
            public void onDirectoryCreate(File directory) {
                System.out.println("onDirectoryCreate:" + directory.getName());
            }

            @Override
            public void onDirectoryChange(File directory) {
                System.out.println("onDirectoryChange:" + directory.getName());
            }

            @Override
            public void onDirectoryDelete(File directory) {
                System.out.println("onDirectoryDelete:" + directory.getName());
            }

            @Override
            public void onFileCreate(File file) {
                System.out.println("onFileCreate:" + file.getPath()+","+file.getName());
            }

            @Override
            public void onFileChange(File file) {
                System.out.println("onFileChange:" + file.getPath()+","+file.getName());
            }

            @Override
            public void onFileDelete(File file) {
                System.out.println("onFileDelete :" + file.getName());
            }

            @Override
            public void onStop(FileAlterationObserver observer) {
//                System.out.println("onStop");
            }
        });
        m.start();
    }
}
