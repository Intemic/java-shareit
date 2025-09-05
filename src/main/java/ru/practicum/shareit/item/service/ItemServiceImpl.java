package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenResource;
import ru.practicum.shareit.exception.NotApproved;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public Item getOneItem(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty())
            throw new NotFoundResource("Не найдена вещь %d".formatted(id));

        return optionalItem.get();
    }

    private ItemDto setCommentsDto(ItemDto item, List<Comment> comments) {
        item.setComments(comments.stream()
                .map(CommentMapper::mapToDto)
                .toList());
        return item;
    }

    @Override
    public ItemDto getItem(Long id) {
        ItemDto itemDto = ItemMapper.mapToDto(getOneItem(id));
        setCommentsDto(itemDto, commentRepository.findAllByItem(id));
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        Map<Long, Item> itemMap = itemRepository.findByOwnerId(userId).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<Comment>> commentMap = commentRepository.findAllByItemIn(itemMap.keySet()).stream()
                .collect(Collectors.groupingBy(Comment::getItem));

        return itemMap.values().stream()
                .map(item -> setCommentsDto(ItemMapper.mapToDto(item),
                        commentMap.getOrDefault(item.getId(), Collections.emptyList())))
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemDto create(ItemCreate item, long userId) {
        item.setOwner(userService.getOneUser(userId));
        return ItemMapper.mapToDto(itemRepository.save(ItemMapper.mapToItem(item)));
    }

    @Override
    public ItemDto update(ItemUpdate item, long userId) {
        Item oldItem = getOneItem(item.getId());
        if (oldItem.getOwner().getId() != userId)
            throw new ForbiddenResource("Отсутствуют полномочия на операцию");

        Item updateItem = ItemMapper.updateItem(item, oldItem);
        itemRepository.save(updateItem);
        return ItemMapper.mapToDto(updateItem);
    }

    @Override
    public CommentDto createComment(CommentCreate comment) {
        //проверки
        getOneItem(comment.getItemId());
        User user = userService.getOneUser(comment.getAuthorId());
        // проверяем что брал в аренду
        if (bookingRepository.findAllByItemIdAndEndBeforeAndBookerIdAndStatus(comment.getItemId(),
                LocalDateTime.now(), comment.getAuthorId(), BookingStatus.APPROVED).isEmpty())
            throw new NotApproved("Недопустимая операция");

        return CommentMapper.mapToDto(commentRepository.save(CommentMapper.mapToComment(comment, user)));
    }
}
