package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    private LocalDateTime created;
    private String description;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="request_id")
    private final List<Item> items = new ArrayList<>();
}
