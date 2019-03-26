package one.aitec.ddb.storage;

import one.aitec.ddb.SerializablePredicate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InMemoryStorage implements Storage {


    private Map<String, Map<Long, Storable>> itemsMap = new ConcurrentHashMap<>();


    public Storable get(String cache, Long id) {
        Map<Long, Storable> items = itemsMap.get(cache);
        if (items != null) {
            return items.get(id);
        }
        return null;
    }


    public <T extends Storable> List<T> find(String cache,
                                             SerializablePredicate<T> predicate,
                                             final Class<T> type) {
        Map<Long, Storable> items = itemsMap.get(cache);
        if (items != null) {
            return items.values()
                    .stream()
                    .filter(type::isInstance)
                    .map(type::cast)
                    .filter(predicate)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public <T extends Storable> Optional<T> findFirst(String cache,
                                                      SerializablePredicate<T> predicate,
                                                      Class<T> type) {
        Map<Long, Storable> items = itemsMap.get(cache);
        if (items != null) {
            return items.values()
                    .stream()
                    .filter(type::isInstance)
                    .map(type::cast)
                    .filter(predicate)
                    .findFirst();
        }
        return Optional.empty();
    }


    @SuppressWarnings("unchecked")
    public <T extends Storable> void update(String cache, SerializablePredicate<T> predicate, T change) {
        Class<T> type = (Class<T>) change.getClass();
        List<T> list = find(cache, predicate, type);
        list.forEach(i -> i.update(change));
    }


    public void remove(String cache, Long id) {
        Map<Long, Storable> items = itemsMap.get(cache);
        if (items != null) {
            items.remove(id);
        }
    }


    public void put(String cache, Storable item) {
        Map<Long, Storable> items = itemsMap.get(cache);
        if (items == null) {
            items = new ConcurrentHashMap<>();
            itemsMap.put(cache, items);
        }

        items.put(item.getId(), item);
    }


}
