package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @Column(name = "booking_start")
    private LocalDateTime start;
    @Column(name = "booking_end")
    private LocalDateTime end;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
