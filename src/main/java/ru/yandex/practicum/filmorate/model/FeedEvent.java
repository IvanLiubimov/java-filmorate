package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.FeedEventOperation;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedEvent {
    private Long eventId;          // ID события
    private Long userId;          // ID пользователя
    private FeedEventType eventType;     // Тип события
    private FeedEventOperation operation; // Тип операции
    private Long entityId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)// ID связанной сущности
    private long timestamp; // Время события
}