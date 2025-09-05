package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStatusFilter;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ErrorParameter;
import ru.practicum.shareit.exception.ForbiddenResource;
import ru.practicum.shareit.exception.NotAvailable;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    private Booking getOneBooking(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isEmpty())
            throw new NotFoundResource("Не найдена бронь %d".formatted(id));

        return optionalBooking.get();
    }

    @Override
    public BookingDto getBooking(long id, long userId) {
        // проверка
        userService.getOneUser(userId);
        Booking booking = getOneBooking(id);
        if ((booking.getBooker().getId() != userId) && (booking.getItem().getOwner().getId() != userId))
            throw new ForbiddenResource("Отсутствуют полномочия на операцию");

        return BookingMapper.mapToDto(booking);
    }

    private List<BookingDto> filterAndSortBooking(List<Booking> bookings, BookingStatusFilter statusFilter) {
        return bookings.stream()
                .filter(booking -> switch (statusFilter) {
                    case ALL -> true;
                    case CURRENT -> booking.getStatus().equals(BookingStatus.APPROVED);
                    case PAST -> booking.getEnd().isBefore(LocalDateTime.now());
                    case FUTURE -> booking.getStart().isAfter(LocalDateTime.now());
                    case WAITING -> booking.getStatus().equals(BookingStatus.WAITING);
                    case REJECTED -> booking.getStatus().equals(BookingStatus.REJECTED);
                })
                .map(BookingMapper::mapToDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .toList();
    }

    public List<BookingDto> getBooking(long userId, BookingStatusFilter statusFilter) {
        // проверка
        userService.getOneUser(userId);
        return filterAndSortBooking(bookingRepository.findAllByBookerId(userId), statusFilter);
    }

    public List<BookingDto> getBookingOwner(long userId, BookingStatusFilter statusFilter) {
        // проверка
        userService.getOneUser(userId);
        return filterAndSortBooking(bookingRepository.findAllByItemOwnerId(userId), statusFilter);
    }

    private void checkData(Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now()))
            throw new ErrorParameter("Дата начала не может быть раньше текущей");

        if (booking.getEnd().isBefore(LocalDateTime.now()))
            throw new ErrorParameter("Дата окончания не может быть раньше текущей");

        if (booking.getEnd().isEqual(booking.getStart()) || booking.getEnd().isBefore(booking.getStart()))
            throw new ErrorParameter("Дата окончания должна быть больше даты начала");
    }

    @Override
    public BookingDto create(BookingCreate booking) {
        // проверка
        userService.getOneUser(booking.getBooker());
        if (!itemService.getOneItem(booking.getItemId()).isAvailable())
            throw new NotAvailable("Операция невозможна, вещь не доступна");

        Booking bookingCreate = BookingMapper.mapToBooking(booking,
                userService,
                itemService);
        checkData(bookingCreate);
        return BookingMapper.mapToDto(bookingRepository.save(bookingCreate));
    }

    public BookingDto change_approved(long bookingId, long userId, String approvedStr) {
        Booking booking = getOneBooking(bookingId);
        if (booking.getItem().getOwner().getId() != userId)
            throw new ForbiddenResource("Недопустимая операция");

        if (approvedStr.equals("true"))
            booking.setStatus(BookingStatus.APPROVED);
        else if (approvedStr.equals("false"))
            booking.setStatus(BookingStatus.REJECTED);

        return BookingMapper.mapToDto(bookingRepository.save(booking));
    }
}
