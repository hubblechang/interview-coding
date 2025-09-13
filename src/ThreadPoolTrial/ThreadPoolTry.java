package ThreadPoolTrial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ThreadPoolTry {
    public static void main(String[] args) {
        threadPoolGeneralUsage();
    }

    public static void futureReturn() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<String>> futures = new ArrayList<>();

// 提交任务（同上）
        for (int i = 0; i < 5; i++) {
            int taskId = i;
            futures.add(executor.submit(() -> {
                Thread.sleep(new Random().nextInt(1000));
                return "任务" + taskId + "完成";
            }));
        }

// 循环检查所有任务是否完成
        int completed = 0;
        while (completed < 5) {
            for (Future<String> future : futures) {
                if (future.isDone() && !future.isCancelled()) {
                    try {
                        System.out.println("获取结果：" + future.get());
                        completed++;
                        // 标记已处理的future，避免重复处理
                        futures.remove(future);
                        break; // 退出当前循环，重新检查剩余任务
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Thread.sleep(100); // 避免空轮询
        }

        executor.shutdown();

    }

    public static void threadPoolGeneralUsage() {
        // 创建 ThreadPoolExecutor 的常见配置
        int corePoolSize = 3;           // 核心线程数
        int maximumPoolSize = 5;        // 最大线程数
        long keepAliveTime = 10;        // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(10); // 任务队列
        ThreadFactory threadFactory = Executors.defaultThreadFactory();    // 线程工厂
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy(); // 拒绝策略

        // 创建 ThreadPoolExecutor 实例
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );

        // 提交多个任务
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("执行任务 " + taskId + "，线程：" + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000); // 模拟任务执行
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("任务 " + taskId + " 执行完成");
            });
        }

        // 关闭线程池
        executor.shutdown();
        try {
            // 等待所有任务完成
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // 强制关闭
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
