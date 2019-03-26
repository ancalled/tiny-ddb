package one.aitec.ddb.storage;

import one.aitec.ddb.SerializablePredicate;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface ReadableStorage {


    Storable get(String cache, Long id);

    <T extends Storable> List<T> find(String cache,
                                      SerializablePredicate<T> predicate,
                                      final Class<T> type);

    <T extends Storable> Optional<T> findFirst(String cache,
                                               SerializablePredicate<T> predicate,
                                               Class<T> type);

}
