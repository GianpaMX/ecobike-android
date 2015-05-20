package mx.softux.ecobike;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import rx.Observable;
import rx.Subscriber;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;

public class Station {
    public Integer number;

    public Station() {
        number = null;
    }

    public static Station fromStream(InputStream inputStream) {
        return Observable.just(inputStream).map(new Func1<InputStream, JsonParser>() {
            @Override
            public JsonParser call(InputStream inputStream) {
                try {
                    return new ObjectMapper().getFactory().createParser(inputStream);
                } catch (IOException e) {
                    throw OnErrorThrowable.from(e);
                }
            }
        }).flatMap(new Func1<JsonParser, Observable<Station>>() {
            @Override
            public Observable<Station> call(final JsonParser jsonParser) {
                return Observable.create(new Observable.OnSubscribe<Station>() {
                    @Override
                    public void call(Subscriber<? super Station> subscriber) {
                        Station station = new Station();
                        try {
                            if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
                                subscriber.onError(new IOException("Expected " + JsonToken.START_OBJECT + " but got " + jsonParser.getCurrentToken()));
                                return;
                            }

                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                ignoreObjectOrArray();

                                String fieldName = jsonParser.getCurrentName();

                                if ("number".equals(fieldName)) {
                                    jsonParser.nextToken();
                                    station.number = jsonParser.getValueAsInt();
                                }
                            }
                            subscriber.onNext(station);

                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }

                    private void ignoreObjectOrArray() throws IOException {
                        if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT)
                            ignoreObject(jsonParser);

                        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY)
                            ignoreArray(jsonParser);
                    }

                    public void ignoreObject(JsonParser jsonParser) throws IOException {
                        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT)
                            throw new IOException("Expected " + JsonToken.START_OBJECT + " but got " + jsonParser.getCurrentToken());

                        while (jsonParser.nextToken() != JsonToken.END_OBJECT)
                            ignoreObjectOrArray();
                    }

                    public void ignoreArray(JsonParser jsonParser) throws IOException {
                        if (jsonParser.getCurrentToken() != JsonToken.START_ARRAY)
                            throw new IOException("Expected " + JsonToken.START_ARRAY + " but got " + jsonParser.getCurrentToken());

                        while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                            ignoreObjectOrArray();
                    }
                });
            }
        }).toBlocking().first();
    }
}
