package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.time.Duration;
import java.io.IOException;

public class CustomDurationDeserializer extends StdDeserializer<Duration> {
    public CustomDurationDeserializer() {
        this(null);
    }

    public CustomDurationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Duration deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        try {
            long minutes = jp.getLongValue(); // Получаем число (например, 100)
            return Duration.ofMinutes(minutes); // Преобразуем в Duration (100 минут)
        } catch (Exception e) {
            throw new IOException("Invalid duration format: " + jp.getText(), e);
        }
    }
}