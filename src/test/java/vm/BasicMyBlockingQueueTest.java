package vm;

import org.junit.Assert;
import org.junit.Test;

public class BasicMyBlockingQueueTest {
	@Test
	public void testCapacity() throws InterruptedException {

		int capacity = 100;
		MyBlockingQueue<Integer> queue = new MyBlockingQueue<Integer>(capacity);

		Producer1 producer = new Producer1(queue);
		Thread t1 = new Thread(producer);
		t1.start();
		Thread.sleep(1000);
		t1.interrupt();
		Assert.assertEquals(capacity, queue.size());
	}

	@Test(expected = InterruptedException.class)
	public void testBLocking() throws InterruptedException {

		int capacity = 100;
		MyBlockingQueue<Integer> queue = new MyBlockingQueue<Integer>(capacity);

		final Thread testThread = Thread.currentThread();
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				testThread.interrupt();
			}

		}.start();
		queue.take();
	}

	public static class Producer1 implements Runnable {
		protected MyBlockingQueue<Integer> queue = null;

		public Producer1(MyBlockingQueue<Integer> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			try {
				while (true) {
					queue.put(1);
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
