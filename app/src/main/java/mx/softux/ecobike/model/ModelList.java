package mx.softux.ecobike.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by gianpa on 12/30/14.
 */
public abstract class ModelList<D> extends ArrayList<D> implements Jsonable {

    public ModelList() {
        super();
    }

    public ModelList(int capacity) {
        super(capacity);
    }

    public ModelList(Collection<? extends D> collection) {
        super(collection);
    }

}
