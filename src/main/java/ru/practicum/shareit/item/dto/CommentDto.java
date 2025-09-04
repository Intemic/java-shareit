package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@Data
public class CommentDto {
    private long id;
    private String authorName;
    private LocalDateTime created;
    private String text;

    public CommentDto getFromComment(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getText());
    }
}
