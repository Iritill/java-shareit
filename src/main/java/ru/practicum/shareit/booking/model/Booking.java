package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Builder
@Data
public class Booking {
    private Long id;
    private Item item;
    private LocalDate startBooking;
    private LocalDate endBooking;
    private User booker;
    private BookingStatus status;
}
