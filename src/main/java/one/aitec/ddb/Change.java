package one.aitec.ddb;

import one.aitec.ddb.replicator.Sendable;
import one.aitec.ddb.storage.Storable;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

public class Change<T extends Storable> implements Sendable, Serializable {

    public enum Action {
        PUT, UPDATE, REMOVE
    }

    public String cache;
    public Action action;
    public Long id;
    public SerializablePredicate<T> predicate;
    public T item;
    public T change;
    public transient CountDownLatch latch;

    public Change(byte[] bytes) {
        deserialize(bytes);
    }

    public Change(String cache, Action action, T item, CountDownLatch latch) {
        this.cache = cache;
        this.action = action;
        this.item = item;
        this.latch = latch;
    }


    public Change(String cache, Action action, SerializablePredicate<T> predicate, T change, CountDownLatch latch) {
        this.cache = cache;
        this.action = action;
        this.predicate = predicate;
        this.change = change;
        this.latch = latch;
    }


    public Change(String cache, Action action, Long id, CountDownLatch latch) {
        this.cache = cache;
        this.action = action;
        this.id = id;
        this.latch = latch;
    }

    @Override
    public void await() throws InterruptedException {
        if (latch != null) {
            latch.await();
        }
    }

    @Override
    public void countDown() {
        if (latch != null) {
            latch.countDown();
        }
    }

    @Override
    public byte[] serialize() {
        try {
            return SerializationUtils.toBytes(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deserialize(byte[] bytes) {
        try {
            Change<T> change = (Change<T>) SerializationUtils.fromBytes(bytes);
            this.cache = change.cache;
            this.action = change.action;
            this.predicate = change.predicate;
            this.change = change.change;
            this.item = change.item;
            this.id = change.id;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "Change{" +
                "cache='" + cache + '\'' +
                ", action=" + action +
                ", id=" + id +
                ", predicate=" + predicate +
                ", item=" + item +
                ", change=" + change +
                '}';
    }
}
