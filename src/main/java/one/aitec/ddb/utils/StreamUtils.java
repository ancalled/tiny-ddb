package one.aitec.ddb.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class StreamUtils {

    @SafeVarargs
    public static final <T> Map<String, T> joinMaps(BinaryOperator<T> combiner, Map<String, T>... maps) {

        Map<String, T> finalMap = new HashMap<>();
        if (maps.length > 0) {
            finalMap.putAll(maps[0]);

            for (int i = 1; i < maps.length; i++) {
                Map<String, T> map = maps[i];
                for (Map.Entry<String, T> e : map.entrySet()) {
                    T item = finalMap.get(e.getKey());
                    T newItem = e.getValue();
                    if (item == null) {
                        finalMap.put(e.getKey(), newItem);

                    } else {
                        combiner.apply(item, newItem);
                    }
                }
            }
        }
        return finalMap;
    }


    @SafeVarargs
    public static final <T> Map<String, Map<String, T>> joinNestedMaps(BinaryOperator<T> combiner,
                                                                       Map<String, Map<String, T>>... maps) {

        return joinMaps((a, b) -> joinMaps(combiner, a, b), maps);
    }


    @SafeVarargs
    public static final <T> Map<String, Map<String, Map<String, T>>> joinNested2(BinaryOperator<T> combiner,
                                                                                 Map<String, Map<String, Map<String, T>>>... maps) {

        return joinMaps((a, b) -> joinMaps((aa, bb) -> joinMaps(combiner, aa, bb), a, b), maps);
    }


    public static <T, K> Map<String, K> resolvePath(T t, Function<T, Map<String, K>> resolver) {
        return resolver.apply(t);
    }

    public static <T> Map<String, T> simpleMap(String key, T value) {
        Map<String, T> map = new HashMap<>();
        map.put(key, value);
        return map;
    }





    private static class TestObj {
        String id;
        String value;

        @Override
        public String toString() {
            return "TestObj{" +
                    "id='" + id + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


    public static void main(String[] args) {

        TestObj obj = new TestObj();
        obj.id = "1";
        obj.value = "test value";

        Map<String, Map<String, TestObj>> mp = simpleMap(obj.getClass().getSimpleName(), simpleMap(obj.id, obj));
        System.out.println(mp);


    }

    private static Function<TestObj, Map<String, Map<String, TestObj>>> resolveByClassNameAndId =
            o -> simpleMap(o.getClass().getSimpleName(), simpleMap(o.id, o));
}
