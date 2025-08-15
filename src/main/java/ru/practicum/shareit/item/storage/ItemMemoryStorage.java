package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class ItemMemoryStorage implements ItemStorage {
    private static Long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return items.replace(item.getId(), item);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> getAll() {
        return List.of();
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        Pattern pattern = Pattern.compile("(\\w*)" + text + "(\\w*)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        return text.isBlank() ? List.of() :
                items.values().stream()
                        .filter(item ->
                                (pattern.matcher(item.getName()).matches()
                                        || pattern.matcher(item.getDescription()).matches()) && item.isAvailable()
                        )
                        .toList();
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }
}
