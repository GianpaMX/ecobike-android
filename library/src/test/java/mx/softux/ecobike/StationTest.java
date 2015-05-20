package mx.softux.ecobike;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gianpa on 5/15/15.
 */
public class StationTest {
    @Test
    public void createStation() {
        Station station = new Station();
        Assert.assertNotNull(station);
    }

    @Test
    public void statiomFromFile() {
        try {
            InputStream inputStream = Resources.getResourceAsStream("json/station.json");

            Station station = Station.fromStream(inputStream);

            inputStream.close();

            Assert.assertTrue(station instanceof Station);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("InputStream.close()");
        }
    }
}
