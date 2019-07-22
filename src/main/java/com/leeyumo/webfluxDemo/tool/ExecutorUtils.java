package com.leeyumo.webfluxDemo.tool;

import java.util.concurrent.TimeUnit;

public class ExecutorUtils {

    public static void sleepTwoSeconds(){
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
