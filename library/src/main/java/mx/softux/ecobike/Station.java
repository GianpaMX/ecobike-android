package mx.softux.ecobike;

public class Station {
    public Integer number;
    public Status status;

    public Station() {
        number = null;
        status = null;
    }

    public enum Status {
        OPN
    }
}
