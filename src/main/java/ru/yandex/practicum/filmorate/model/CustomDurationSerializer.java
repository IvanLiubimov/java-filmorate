package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.time.Duration;
import java.io.IOException;

public class CustomDurationSerializer extends StdSerializer<Duration> {
    public CustomDurationSerializer() {
        this(null);
    }

    public CustomDurationSerializer(Class<Duration> t) {
        super(t);
    }

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.toMinutes()); // Сохраняем Duration как количество минут
    }
}