package one.aitec.ddb.replicator;

import one.aitec.ddb.Executable;

public interface Sendable extends Executable {

    byte[] serialize();

    void deserialize(byte[] bytes);

}
