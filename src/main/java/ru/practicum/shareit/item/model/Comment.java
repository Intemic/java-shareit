package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Builder
@Data
public class Comment {
    @Id
    private long id;
    @Column(name = "item_id")
    private long item;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    private String text;
    private LocalDateTime created;
}
