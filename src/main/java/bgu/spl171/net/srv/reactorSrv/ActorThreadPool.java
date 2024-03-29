package bgu.spl171.net.srv.reactorSrv;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * This is the Thread pool used by the reactor server
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class ActorThreadPool {

    private final Map<Object, Queue<Runnable>> acts;
    private final ReadWriteLock actsRWLock;
    private final Set<Object> playingNow;
    private final ExecutorService threads;

    /**
     * Constructor
     * @param threads the given number of threads
     */
    public ActorThreadPool(int threads) {
        this.threads = Executors.newFixedThreadPool(threads);
        acts = new WeakHashMap<>();
        playingNow = ConcurrentHashMap.newKeySet();
        actsRWLock = new ReentrantReadWriteLock();
    }

    /**
     * submits a new task
     * @param act the object associated with the task
     * @param r the task to b submitted
     */
    public void submit(Object act, Runnable r) {
        synchronized (act) {
            if (!playingNow.contains(act)) {
                playingNow.add(act);
                execute(r, act);
            } else {
                pendingRunnablesOf(act).add(r);
            }
        }
    }

    /**
     * Shuts down the thread pool
     */
    public void shutdown() {
        threads.shutdownNow();
    }

    private Queue<Runnable> pendingRunnablesOf(Object act) {
        actsRWLock.readLock().lock();
        Queue<Runnable> pendingRunnables = acts.get(act);
        actsRWLock.readLock().unlock();

        if (pendingRunnables == null) {
            actsRWLock.writeLock().lock();
            acts.put(act, pendingRunnables = new LinkedList<>());
            actsRWLock.writeLock().unlock();
        }
        return pendingRunnables;
    }

    private void execute(Runnable r, Object act) {
        threads.execute(() -> {
            try {
                r.run();
            } finally {
                complete(act);
            }
        });
    }

    private void complete(Object act) {
        synchronized (act) {
            Queue<Runnable> pending = pendingRunnablesOf(act);
            if (pending.isEmpty()) {
                playingNow.remove(act);
            } else {
                execute(pending.poll(), act);
            }
        }
    }

}
