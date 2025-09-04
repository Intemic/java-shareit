package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }

    public static Comment mapToComment(CommentCreate commentCreate, User author) {
        return Comment.builder()
                .item(commentCreate.getItemId())
                .author(author)
                .text(commentCreate.getText())
                .created(commentCreate.getCreated())
                .build();
    }

}
