package one.aitec.ddb;

import one.aitec.ddb.replicator.Replicator;
import one.aitec.ddb.storage.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

import static one.aitec.ddb.Change.Action.*;

public class StorageProxy implements Storage {

    public static final int PROCESS_STEPS = 2;

    private SingleWriter sw;
    private ReadableStorage rs;
    private Replicator replicator;


    public StorageProxy(SingleWriter sw, ReadableStorage readableStorage, Replicator replicator) {
        this.sw = sw;
        this.rs = readableStorage;
        this.replicator = replicator;
        if (sw == null) throw new StorageRuntimeException("SingleWriter is null!");
        if (rs == null) throw new StorageRuntimeException("ReadableStore is null!");
        if (replicator == null) throw new StorageRuntimeException("Sender is null!");
    }

    @Override
    public <T extends Storable> void update(String cache, SerializablePredicate<T> predicate, T change) {
        publishAndWait(new Change<>(cache, UPDATE, predicate, change, new CountDownLatch(PROCESS_STEPS)));

    }

    @Override
    public void remove(String cache, Long id) {
        publishAndWait(new Change<>(cache, REMOVE, id, new CountDownLatch(PROCESS_STEPS)));
    }

    @Override
    public void put(String cache, Storable item) {
        publishAndWait(new Change<>(cache, PUT, item, new CountDownLatch(PROCESS_STEPS)));
    }

    private void publishAndWait(Change c) {
        try {
            sw.publish(c);
            replicator.publish(c);
            c.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Storable get(String cache, Long id) {
        return rs.get(cache, id);
    }

    @Override
    public <T extends Storable> List<T> find(String cache, SerializablePredicate<T> predicate, Class<T> type) {
        return rs.find(cache, predicate, type);
    }

    @Override
    public <T extends Storable> Optional<T> findFirst(String cache, SerializablePredicate<T> predicate, Class<T> type) {
        return rs.findFirst(cache, predicate, type);
    }
}
