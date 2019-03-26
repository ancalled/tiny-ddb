package one.aitec.ddb.storage;

import one.aitec.ddb.Change;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import static one.aitec.ddb.Change.Action.*;

public class SingleWriter implements Runnable {

    private final BlockingQueue<Change> queue = new ArrayBlockingQueue<>(1000);

    private final String node;
    private final WritableStorage ws;

    public SingleWriter(String node, WritableStorage ws) {
        this.node = node;
        this.ws = ws;
    }

    public void start() {
        Executors.newSingleThreadExecutor().execute(this);
    }

    public void publish(Change change) {
        queue.add(change);
    }


    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Change change = queue.take();
                process(change);
                change.countDown();
            } catch (Throwable t) {
                t.printStackTrace();
                break;
            }
        }
    }

    private void process(Change change) {
        System.out.printf("[%s] SingleWriter processing change: %s \n", node, change);
        if (change.action == PUT) {
            ws.put(change.cache, change.item);

        } else if (change.action == UPDATE) {
            ws.update(change.cache, change.predicate, change.change);

        } else if (change.action == REMOVE) {
            ws.remove(change.cache, change.id);
        }

    }


}
