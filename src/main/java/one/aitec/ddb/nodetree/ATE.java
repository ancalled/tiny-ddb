package one.aitec.ddb.nodetree;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.rangeClosed;

/**
 * AbstractTreeElement aka ATE
 *
 * @param <T>
 */
public abstract class ATE<T> {

    public final ATE parent;
    public final String key;
    protected long version;
    private final Map<Long, Modification> modifications = new TreeMap<>();

    public ATE(TreeNode parent, String key) {
        this.parent = parent;
        this.key = key;
    }

    protected void onModification(Modification mod, boolean propagate) {
        if (mod != null) {
            version++;
            mod.version = version;  //todo set global version
            modifications.put(version, mod);
            if (propagate && parent != null) {
                parent.onModification(mod, true);
            }
        }
    }

    public abstract T getData();

    protected static String symbols(String symbol, int ntimes) {
        return rangeClosed(1, ntimes)
                .mapToObj(a -> symbol)
                .collect(joining());
    }

    protected abstract boolean applyModification(Modification modification);


    public boolean checkAndUpdate(ATE<T> tree) {
        if (version > tree.version) {
            List<Modification> lst = lostModifications(tree.version);
            return lst.stream().anyMatch(tree::applyModification);
        }

        return false;
    }

    private List<Modification> lostModifications(long fromVersion) {
        return modifications.entrySet().stream().filter(e -> e.getKey() > fromVersion)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }


    protected abstract String print(int level);

}
