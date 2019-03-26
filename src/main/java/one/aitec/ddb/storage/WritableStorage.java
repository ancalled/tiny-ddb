package one.aitec.ddb.storage;

import one.aitec.ddb.SerializablePredicate;


public interface WritableStorage {

    <T extends Storable> void update(String cache, SerializablePredicate<T> predicate, T change);

    void remove(String cache, Long id);

    void put(String cache, Storable item);
}
