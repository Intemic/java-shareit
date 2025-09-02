package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Builder
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    List<Item> items = new ArrayList<Item>();

    public void addItem(Item item) {
        items.add(item);
        item.setOwner(this);
    }

    public void deleteItem(Item item) {
        item.setOwner(null);
        items.remove(item);
    }
}

