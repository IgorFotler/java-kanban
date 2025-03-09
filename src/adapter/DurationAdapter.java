package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(Long.toString(duration.toMinutes()));
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        if (jsonReader != null) {
            return Duration.ofMinutes(jsonReader.nextLong());
        } else {
            return null;
        }
    }
}