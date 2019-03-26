package one.aitec.ddb.replicator;



import one.aitec.ddb.Change;
import one.aitec.ddb.replicator.transport.MessageListener;
import one.aitec.ddb.replicator.transport.Transport;
import one.aitec.ddb.storage.SingleWriter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

public class Replicator implements Runnable, MessageListener {

    public static final String REPLICATOR_TOPIC = "db-replicator";

    private final String node;
    private final Transport transport;
    private final SingleWriter writer;


    private final BlockingQueue<Change> queue = new ArrayBlockingQueue<>(1000);

    public Replicator(String node, Transport transport, SingleWriter writer) {
        this.node = node;
        this.transport = transport;
        this.writer = writer;
        transport.addListener(this);
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
                send(change);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void send(Change change) {
        System.out.printf("[%s] Replicator sending change: %s\n", node, change);
        byte[] body = change.serialize();
        transport.publish(node, REPLICATOR_TOPIC, body);
        change.countDown();
    }

    @Override
    public void onMessage(String sender, String topic, byte[] bytes) {
        if (!node.equals(sender)) {
            if (REPLICATOR_TOPIC.equals(topic)) {
                Change change = new Change(bytes);
                System.out.printf("[%s] Replicator onMessage received change: %s\n", node, change);
                writer.publish(change);
            }
        }
    }

}
