package reactor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        /**
         * CountDownLatch最主要的两个方法：countDown()和 await()
         * 这个例子可以体验到CountDownLatch的用处(修改构造时数量参数)
         * 每个线程都可以countDown,使声明时的倒数数量-1，当到0的时候，会出发await，但await必须在主线程中，await后代码才会向下执行
         */
        CountDownLatch latch = new CountDownLatch(4);
        Service service = new Service(latch);
        Runnable task = () -> service.exec();

        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(task);
            thread.start();
        }

        System.out.println("main thread await. ");
        latch.await();
        System.out.println("main thread finishes await. ");
    }

}

class Service {
    private CountDownLatch latch;

    public Service(CountDownLatch latch) {
        this.latch = latch;
    }

    public void exec() {
        try {
            System.out.println(Thread.currentThread().getName() + " execute task. ");
            sleep(2);
            System.out.println(Thread.currentThread().getName() + " finished task. ");
        } finally {
            latch.countDown();
        }
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
