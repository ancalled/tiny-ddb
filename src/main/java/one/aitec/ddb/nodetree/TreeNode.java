package one.aitec.ddb.nodetree;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeNode<T> extends ATE<T> {

    private final Map<String, ATE<T>> elements = new ConcurrentHashMap<>();

    public TreeNode() {
        this(null, "/");
    }

    public TreeNode(TreeNode parent, String key) {
        super(parent, key);
    }

    @Override
    public T getData() {
        return null;
    }

    public <K> TreeNode<T> put(K item) {
        return put(PathParser.DEFAULT_TEMPLATE, item);
    }

    public <K> TreeNode<T> put(String path, K item) {
        return put(path, item, null);
    }

    public <K> TreeNode<T> put(String path, K item, OnResultCallback callback) {
        Modification modification = new Modification(OpType.PUT, path, item);
        return put(PathParser.parsePath(path, item), item, modification, callback);
    }

    private <K> TreeNode<T> put(List<String> path, K item, Modification modification, OnResultCallback callback) {
        if (path == null || path.isEmpty()) return null;

        String key = path.remove(0);
        if (path.size() >= 1) {
            ATE<T> element = getElement(key);
            TreeNode childNode = null;
            if (element == null) {
                childNode = new TreeNode(this, key);
                //noinspection unchecked
                putElement(key, childNode);
            } else {
                if (element instanceof TreeNode) {
                    childNode = (TreeNode) element;
                }
            }
            
            if (childNode != null) {
                childNode.put(path, item, modification, callback);
            }
        } else {
            //noinspection unchecked
            TreeLeaf childNode = new TreeLeaf(this, key, item);
            //noinspection unchecked
            putElement(key, childNode, modification, callback);
        }
        return this;
    }

    public TreeNode<T> putElement(String key, ATE<T> item) {
        return putElement(key, item, null, null);
    }

    public TreeNode<T> putElement(String key, ATE<T> item, Modification modification, OnResultCallback callback) {
        //noinspection unchecked
        elements.put(key, item);
        onModification(modification, true);
        if (callback != null) {
            callback.onResult(new UpdateResult(OpType.PUT, this.key, version, key));
        }
        return this;
    }

    public TreeNode<T> remove(String path) {
        return remove(path, null);
    }

    public TreeNode<T> remove(String path, OnResultCallback callback) {
        Modification modification = new Modification(OpType.REMOVE, path);
        return remove(PathParser.parsePath(path), modification, callback);
    }

    private TreeNode<T> remove(List<String> path, Modification modification, OnResultCallback callback) {
        TreeNode node = findNode(path);
        if (node != null) {
            node.removeElement(path.get(0), modification, callback);
        }
        return this;
    }

    public TreeNode<T> removeElement(String key) {
        return removeElement(key, null, null);
    }

    public TreeNode<T> removeElement(String key, Modification modification, OnResultCallback callback) {
        elements.remove(key);
//        if (modification == null) {
//            modification = new Modification(OpType.REMOVE, key);
//        }
        onModification(modification, true);
        if (callback != null) {
            callback.onResult(new UpdateResult(OpType.REMOVE, this.key, version, key));
        }
        return this;
    }

    public <K> TreeNode<T> update(String path,
                                  Class<K> typeCheck,
                                  Predicate<String> keyPredicate,
                                  Predicate<K> valuePredicate,
                                  Consumer<K> updatingFunction) {
        Modification modification = new Modification(OpType.UPDATE, path,
                typeCheck, keyPredicate, valuePredicate, updatingFunction);
        update(PathParser.parsePath(path), typeCheck, keyPredicate, valuePredicate, updatingFunction,
                modification, null);
        return this;
    }

    public <K> TreeNode<T> update(String path,
                                  Class<K> typeCheck,
                                  Predicate<String> keyPredicate,
                                  Predicate<K> valuePredicate,
                                  Consumer<K> updatingFunction,
                                  OnResultCallback callback) {
        Modification modification = new Modification(OpType.UPDATE, path,
                typeCheck, keyPredicate, valuePredicate, updatingFunction);

        update(PathParser.parsePath(path), typeCheck, keyPredicate, valuePredicate, updatingFunction,
                modification, callback);
        return this;
    }

    public <K> TreeNode<T> update(List<String> path,
                                  Class<K> typeCheck,
                                  Predicate<String> keyPredicate,
                                  Predicate<K> valuePredicate,
                                  Consumer<K> updatingFunction,
                                  Modification modification,
                                  OnResultCallback callback) {
        //noinspection unchecked
        TreeNode<K> node = findNode(path);
        if (node != null) {
            node.updateElement(keyPredicate, valuePredicate, updatingFunction, modification, callback);
        }
        return this;
    }


    public TreeNode<T> updateElement(Predicate<String> keyPredicate,
                                     Predicate<T> valuePredicate,
                                     Consumer<T> updatingFunction) {
        return updateElement(keyPredicate, valuePredicate, updatingFunction, null, null);
    }

    public TreeNode<T> updateElement(Predicate<String> keyPredicate,
                                     Predicate<T> valuePredicate,
                                     Consumer<T> updatingFunction,
                                     Modification modification,
                                     OnResultCallback callback) {


        Stream<Map.Entry<String, ATE<T>>> stream = elements.entrySet().stream();
        if (keyPredicate != null) {
            stream = stream.filter(e -> keyPredicate.test(e.getKey()));
        }
        if (valuePredicate != null) {
            stream = stream.filter(e -> valuePredicate.test(e.getValue().getData()));
        }

        List<Map.Entry<String, ATE<T>>> toUpdate = stream.collect(Collectors.toList());
        toUpdate.stream()
                .filter(e -> e.getValue() instanceof TreeLeaf)
                .map(e -> (TreeLeaf<T>) e.getValue())
                .forEach(l -> l.update(updatingFunction, modification));

        onModification(modification, true);
        if (callback != null) {
            callback.onResult(new UpdateResult(OpType.UPDATE,
                    key,
                    version,
                    toUpdate.stream().map(Map.Entry::getKey)
                            .collect(Collectors.toList())));
        }

        return this;
    }

    public Object findItem(String path) {
        return findItem(path, Object.class);
    }

    public <K> K findItem(String path, Class<K> typeCheck) {
        return findItem(PathParser.parsePath(path), typeCheck);
    }


    private <K> K findItem(List<String> path, Class<K> typeCheck) {
        if (path == null || path.isEmpty()) return null;

        String key = path.remove(0);

        ATE<T> element = getElement(key);

        if (element == null) return null;

        if (path.size() >= 1) {
            if (element instanceof TreeNode) {
                TreeNode node = (TreeNode) element;
                //noinspection unchecked
                return (K) node.findItem(path, typeCheck);
            }
        } else {
            if (typeCheck.isInstance(element.getData())) {
                //noinspection unchecked
                return (K) element.getData();
            }
        }
        return null;
    }


    public TreeNode findNode(String path) {
        return findNode(PathParser.parsePath(path));
    }

    private TreeNode findNode(List<String> path) {
        String key = path.remove(0);

        ATE<T> element = getElement(key);
        if (element != null) {
            if (path.size() > 1) {
                if (element instanceof TreeNode) {
                    TreeNode node = (TreeNode) element;
                    return node.findNode(path);
                }
            } else if (path.size() == 1) {
                if (element instanceof TreeNode) {
                    return (TreeNode) element;
                }
            } else {
                return this;
            }
        }
        return null;
    }

    public ATE<T> getElement(String key) {
        return elements.get(key);
    }

    public Optional<T> findFirstElement(Predicate<T> predicate) {
        return elements.values().stream()
                .map(ATE::getData)
                .filter(predicate)
                .findFirst();
    }

    public List<T> findElement(Predicate<T> predicate) {
        return elements.values().stream()
                .map(ATE::getData)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean applyModification(Modification mod) {
        if (mod.type == OpType.PUT) {
            put(PathParser.parsePath(mod.path, mod.item), mod.item, mod, null);

        } else if (mod.type == OpType.REMOVE) {
            remove(PathParser.parsePath(mod.path), mod, null);

        } else if (mod.type == OpType.UPDATE) {
            //noinspection unchecked
            update(PathParser.parsePath(mod.path), mod.typeCheck,
                    mod.keyPredicate, mod.valuePredicate, mod.updatingFunction,
                    mod, null);
        }
        return false;
    }

    protected String print(int level) {
        StringBuilder buf = new StringBuilder();
        buf.append(symbols("\t", level)).append("/").append(key);
        buf.append("\t[ver: ").append(version).append("]").append("\n");
        elements.forEach((k, v) -> buf.append(v.print(level + 1)));

        return buf.toString();
    }

    @Override
    public String toString() {
        return "TreeNode:\n" + print(0);
    }


}
