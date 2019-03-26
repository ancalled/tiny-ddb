package one.aitec.ddb.storage;

import java.io.Serializable;

public interface Storable extends Serializable {

    Long getId();

    void update(Storable change);
}
