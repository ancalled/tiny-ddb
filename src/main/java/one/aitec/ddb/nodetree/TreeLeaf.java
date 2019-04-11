package one.aitec.ddb.nodetree;

import java.util.function.Consumer;

public class TreeLeaf<T> extends ATE<T> {

    public final T item;

    public TreeLeaf(TreeNode parent, String key, T item) {
        super(parent, key);
        this.item = item;
    }

    void update(Consumer<T> updatingFunction, Modification modification) {
        updatingFunction.accept(item);
        if (modification == null) {
            modification = new Modification(OpType.UPDATE, "");
        }
        onModification(modification, false);
    }

    @Override
    public T getData() {
        return item;
    }

    @Override
    protected boolean applyModification(Modification modification) {
        if (modification.type == OpType.UPDATE) {
            update(modification.updatingFunction, modification);
            return true;
        }
        return false;
    }

    protected String print(int level) {
        return symbols("\t", level) +
                String.format("/%s: %s\t[ver: %d]\n",
                        key,
                        item,
                        version);
    }

    @Override
    public String toString() {
        return "TreeLeaf:\n" + print(0);
    }
}
