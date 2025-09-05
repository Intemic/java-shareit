package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId);

    @Query("select i " +
            "from Item as i " +
            "where (lower(i.name) LIKE lower(%:text%) or " +
            "lower(i.description) LIKE lower(%:text%)) and " +
            "available = true")
    List<Item> search(@Param("text") String text);
}
