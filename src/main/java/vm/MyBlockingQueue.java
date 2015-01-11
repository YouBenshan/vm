package vm;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p><strong>Question:</strong> 
 * Use java concurrency packages to write a queue that supports
 * multiwriter and multireader, i.e. the writer pushes the stuff into the queue
 * and the reader pops the stuff out the queue. All the writer's stuff can't be
 * lost and must be into the queue, and each reader can't pops out the same
 * stuff. Think of how you would simulate the situation and perform the testing.
 * Using the java blocking queue is not allowed.<br/>
 * 
 * <p><strong>Answer:</strong>
 * <p>1. {@link java.util.concurrent.ConcurrentLinkedQueue ConcurrentLinkedQueue} is lock free. It only uses CAS. And it's
 * faster then {@link java.util.concurrent.BlockingQueue BlockingQueue}. But it will not blocked when empty (compared to
 * BlockingQueue). 
 * <p>2. {@link java.util.concurrent.ArrayBlockingQueue ArrayBlockingQueue} has 1 ReentrantLock (like MyBlockingQueue
 * below), and use an array, the cells of array are read/writen cyclic. 
 * <p>3. {@link java.util.concurrent.LinkedBlockingQueue LinkedBlockingQueue} has 2 ReentrantLocks, that reduces lock granularity
 * (compared with ArrayBlockingQueue). It will create new Node to put new element,
 * that will cause more GC actions than ArrayBlockingQueue. 
 * <p>4. {@link MyBlockingQueue} is less effective than ConcurrentLinkedQueue, ArrayBlockingQueue and
 * LinkedBlockingQueue. It's just a demo to use ReentrantLock. And the unit test show how to use it.
 * 
 */

public class MyBlockingQueue<E> {

	private Queue<E> queue = new LinkedList<E>();

	private final int capacity;

	private int count = 0;

	final ReentrantLock lock = new ReentrantLock();

	private final Condition notEmpty = lock.newCondition();

	private final Condition notFull = lock.newCondition();

	public MyBlockingQueue(int capacity) {
		this.capacity = capacity;
	}

	public void put(E e) throws InterruptedException {
		if (e == null) {
			throw new NullPointerException();
		}
		lock.lockInterruptibly();
		try {
			while (count == capacity) {
				notFull.await();
			}
			queue.add(e);
			count++;
			if (count == 1) {
				notEmpty.signalAll();
			}
		} finally {
			lock.unlock();
		}
	}

	public E take() throws InterruptedException {
		lock.lockInterruptibly();
		try {
			while (count == 0) {
				notEmpty.await();
			}
			E e = queue.poll();
			count--;
			if (count == capacity - 1) {
				notFull.signalAll();
			}
			return e;
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		return count;
	}
}