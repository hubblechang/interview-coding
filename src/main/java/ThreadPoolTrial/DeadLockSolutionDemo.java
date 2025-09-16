package ThreadPoolTrial;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockSolutionDemo {
    private static final Lock lockA = new ReentrantLock();
    private static final Lock lockB = new ReentrantLock();
    
    public static void main(String[] args) {
        DeadLockSolutionDemo demo = new DeadLockSolutionDemo();
        demo.avoidDeadlockWithTryLock();
    }
    
    /**
     * 使用 tryLock(timeout) 避免死锁的典型示例
     */
    public static void avoidDeadlockWithTryLock() {
        Thread t1 = new Thread(() -> {
            try {
                // 尝试获取 lockA，最多等待10秒
                if (lockA.tryLock(10, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("线程1获取到lockA");
                        Thread.sleep(2000); // 模拟业务处理
                        
                        // 尝试获取 lockB，最多等待10秒
                        if (lockB.tryLock(10, TimeUnit.SECONDS)) {
                            try {
                                System.out.println("线程1获取到lockB");
                                System.out.println("线程1: 执行业务逻辑");
                            } finally {
                                lockB.unlock();
                                System.out.println("线程1释放lockB");
                            }
                        } else {
                            System.out.println("线程1获取lockB超时，放弃操作");
                        }
                    } finally {
                        lockA.unlock();
                        System.out.println("线程1释放lockA");
                    }
                } else {
                    System.out.println("线程1获取lockA超时，放弃操作");
                }
            } catch (InterruptedException e) {
                System.out.println("线程1被中断");
                Thread.currentThread().interrupt();
            }
        });
        
        Thread t2 = new Thread(() -> {
            try {
                // 尝试获取 lockB，最多等待10秒
                if (lockB.tryLock(10, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("线程2获取到lockB");
                        Thread.sleep(2000); // 模拟业务处理
                        
                        // 尝试获取 lockA，最多等待10秒
                        if (lockA.tryLock(10, TimeUnit.SECONDS)) {
                            try {
                                System.out.println("线程2获取到lockA");
                                System.out.println("线程2: 执行业务逻辑");
                            } finally {
                                lockA.unlock();
                                System.out.println("线程2释放lockA");
                            }
                        } else {
                            System.out.println("线程2获取lockA超时，放弃操作");
                        }
                    } finally {
                        lockB.unlock();
                        System.out.println("线程2释放lockB");
                    }
                } else {
                    System.out.println("线程2获取lockB超时，放弃操作");
                }
            } catch (InterruptedException e) {
                System.out.println("线程2被中断");
                Thread.currentThread().interrupt();
            }
        });
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("程序执行完毕");
    }
}
