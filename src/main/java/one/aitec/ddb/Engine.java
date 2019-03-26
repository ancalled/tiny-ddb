package one.aitec.ddb;

import one.aitec.ddb.replicator.Replicator;
import one.aitec.ddb.replicator.transport.Transport;
import one.aitec.ddb.storage.*;

public class Engine {

    public final Storage storage;

    private final String node;
    private final Replicator replicator;
    private final SingleWriter singleWriter;

    public Engine(String node, Transport transport) {
        this.node = node;
        final InMemoryStorage storage = new InMemoryStorage();
        this.singleWriter = new SingleWriter(node, storage);
        this.replicator = new Replicator(node, transport, singleWriter);
        this.storage = new StorageProxy(singleWriter, storage, replicator);
    }


    public void start() {
        System.out.printf("[%s] Starting engine\n", node);
        singleWriter.start();
        replicator.start();
    }

}
