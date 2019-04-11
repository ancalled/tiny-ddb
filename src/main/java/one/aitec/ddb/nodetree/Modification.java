package one.aitec.ddb.nodetree;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Modification {

    OpType type;
    long version;
    String path;

    Object item;      // PUT

    Class typeCheck;                    //
    Predicate<String> keyPredicate;     //  UPDATE
    Predicate valuePredicate;           //
    Consumer updatingFunction;          //

    public Modification(OpType type, String path) {
        this(type, path, null);
    }

    public Modification(OpType type, String path, Object item) {
        this.type = type;
        this.path = path;
        this.item = DeepCopy.copy(item); //todo this is for test proposes only
    }

    public Modification(OpType type, String path,
                        Class typeCheck,
                        Predicate<String> keyPredicate,
                        Predicate valuePredicate,
                        Consumer updatingFunction) {
        this.type = type;
        this.path = path;
        this.typeCheck = typeCheck;
        this.keyPredicate = keyPredicate;
        this.valuePredicate = valuePredicate;
        this.updatingFunction = updatingFunction;
    }

    @Override
    public String toString() {
        if (type == OpType.PUT) {
            return "Modification{" +
                    "type=" + type +
                    ", version=" + version +
                    ", path='" + path + '\'' +
                    ", item=" + item +
                    '}';

        } else if (type == OpType.REMOVE) {
            return "Modification{" +
                    "type=" + type +
                    ", version=" + version +
                    ", path='" + path + '\'' +
                    '}';

        } else if (type == OpType.UPDATE) {
            return "Modification{" +
                    "type=" + type +
                    ", version=" + version +
                    ", path='" + path + '\'' +
                    ", typeCheck=" + typeCheck +
                    ", keyPredicate=" + keyPredicate +
                    ", valuePredicate=" + valuePredicate +
                    ", updatingFunction=" + updatingFunction +
                    '}';
        }

        return "Modification{" +
                "type=" + type +
                '}';
    }
}
