package vm;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * Some(=workerMount) producers produce number from 0 to {@link MyBlockingQueue}
 * , add them to queue. At the same time some consumers take the numbers from
 * MyBlockingQueue to a {@link java.util.concurrentConcurrentSkipListSet concurrentConcurrentSkipListSet} for verification.
 * <p>{@link java.util.concurrent.CountDownLatch CountDownLatch} startSignal makes Producers start at the same time.
 * <p>{@link java.util.concurrent.CountDownLatch CountDownLatch} doneSignal makes the main thread waiting for producers.
 * <p>The Producer use is lock-free.
 * 
 */

public class ConcurrentMyBlockingQueueTest {
	private int max = 1 << 10;
	private int workerMount = 20;
	private MyBlockingQueue<Integer> queue = new MyBlockingQueue<>(30);
	private AtomicInteger counter = new AtomicInteger(0);
	private CountDownLatch startSignal = new CountDownLatch(1);
	private CountDownLatch doneSignal = new CountDownLatch(workerMount);
	private Set<Integer> result = new ConcurrentSkipListSet<>();

	@Test
	public void test() throws InterruptedException {
		for (int i = 0; i < workerMount; i++) {
			new Thread(new Producer()).start();
			new Thread(new Consumer()).start();
		}
		startSignal.countDown();
		doneSignal.await();
		Thread.sleep(3000);
		Assert.assertEquals(0, queue.size());
		Assert.assertEquals(max, result.size());
		for (int i = 0; i < max; i++) {
			result.contains(i);
		}
	}

	public class Producer implements Runnable {
		@Override
		public void run() {
			try {
				startSignal.await();
				while (true) {
					int c = counter.get();
					if (c == max) {
						break;
					}
					if (counter.compareAndSet(c, c + 1)) {
						
						queue.put(c);
					}
				}
				doneSignal.countDown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public class Consumer implements Runnable {
		@Override
		public void run() {
			try {
				startSignal.await();
				while (true) {
					int i = queue.take();
					result.add(i);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
