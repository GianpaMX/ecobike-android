package mx.softux.ecobike;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gianpa on 5/15/15.
 */
public class StatusTest {
    @Test
    public void createStation() {
        Station station = new Station();
        Assert.assertNotNull(station);
    }

    @Test
    public void statiomFromFile() {
        try {
            InputStream inputStream = Resources.getInstance().getResourceAsStream("json/station.json");
            Assert.assertNotNull("InputStream shoudn't be null", inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("IOException");
        }
    }
}
