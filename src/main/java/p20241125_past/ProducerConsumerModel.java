package p20241125_past;

import java.util.ArrayList;
import java.util.List;

public class ProducerConsumerModel {
    static class Producer implements Runnable {
        List<Integer> queue;
        int maxLength;
        Object lock;
        static int number = 0;

        public Producer(List<Integer> queue, int maxLength, Object lock) {
            this.queue = queue;
            this.maxLength = maxLength;
            this.lock = lock;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    while (queue.size() >= maxLength) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.printf("Producer: add number: %d in queue\n", number);
                    queue.add(number++);
                    lock.notifyAll();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        List<Integer> queue;
        Object lock;

        public Consumer(List<Integer> queue, Object lock) {
            this.queue = queue;
            this.lock = lock;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    while (queue.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.printf("Consumer: consume number: %d in queue\n", queue.remove(0));
                    lock.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> queue = new ArrayList<>();
        int maxLength = 10;
        Object lock = new Object();
        Producer producer = new Producer(queue, maxLength, lock);
        Consumer consumer = new Consumer(queue, lock);
        new Thread(producer).start();
        new Thread(consumer).start();
    }
}
