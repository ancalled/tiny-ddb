package one.aitec.ddb.nodetree;

import java.util.List;

import static java.util.Collections.singletonList;

public class UpdateResult {

    public final OpType opType;
    public final String path;
    public final long newVersion;
    public final List<String> affectedKeys;

    public UpdateResult(OpType opType, String path, long newVersion, List<String> affectedKeys) {
        this.opType = opType;
        this.path = path;
        this.newVersion = newVersion;
        this.affectedKeys = affectedKeys;
    }

    public UpdateResult(OpType opType, String path, long newVersion, String key) {
        this(opType, path, newVersion, singletonList(key));
    }

}
