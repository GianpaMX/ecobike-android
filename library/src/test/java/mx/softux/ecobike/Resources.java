package mx.softux.ecobike;

import java.io.InputStream;

/**
 * Created by gianpa on 5/15/15.
 */
public class Resources {
    private static Resources resources;

    private Resources() {
    }

    public static Resources getInstance() {
        if (resources == null)
            resources = new Resources();

        return resources;
    }

    public InputStream getResourceAsStream(String name) {
        return resources.getClass().getClassLoader().getResourceAsStream(name);
    }
}
