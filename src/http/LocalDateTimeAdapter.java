package http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final String timeDatePattern = "dd.MM.yyyy HH:mm";

    public LocalDateTimeAdapter() {
    }

    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        LocalDateTime value;
        value = Objects.requireNonNullElseGet(localDateTime, LocalDateTime::now);
        jsonWriter.value(value.format(DateTimeFormatter.ofPattern(this.timeDatePattern)));
    }

    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), DateTimeFormatter.ofPattern(this.timeDatePattern));
    }
}