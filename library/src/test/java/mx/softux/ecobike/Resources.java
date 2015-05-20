package mx.softux.ecobike;

import java.io.InputStream;

/**
 * Created by gianpa on 5/15/15.
 */
public class Resources {
    public static InputStream getResourceAsStream(String name) {
        return Resources.class.getResourceAsStream("/resources/" + name);
    }
}
