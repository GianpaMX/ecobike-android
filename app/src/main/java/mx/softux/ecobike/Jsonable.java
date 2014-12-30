package mx.softux.ecobike;

import org.json.JSONObject;

/**
 * Created by gianpa on 12/30/14.
 */
public interface Jsonable {
    public void copyFrom(JSONObject jsonObject);

    public JSONObject toJSONObject();
}
