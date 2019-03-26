package one.aitec.ddb.test;

import one.aitec.ddb.replicator.transport.MessageListener;
import one.aitec.ddb.replicator.transport.Transport;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DummyTransport implements Transport {

    private final List<MessageListener> listeners = new CopyOnWriteArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();


    @Override
    public void publish(String sender, String topic, byte[] bytes) {
        executor.execute(() -> {
            for (MessageListener l : listeners) {
                l.onMessage(sender, topic, bytes);
            }
        });
    }

    @Override
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }
}
