package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(Long ownerId, BookingRequestDto bookingRequestDto) {
        log.info("Create booking request: {}", bookingRequestDto);
        userExist(ownerId);
        validate(ownerId, bookingRequestDto);
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found"));
        return BookingMapper.toBookingDto(bookingRepository.save(Booking.builder().start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build()));
    }

    @Override
    public BookingDto approvedBooking(Long userId, Long bookingId, Boolean isApproved) {
        log.info("Approve booking request: {}", bookingId);
        userExist(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("User isn`t owner");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Booking is already approved");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.info("Get booking request: {}", bookingId);
        userExist(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking id = " + bookingId + " not found!"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner or booker can get Booking!");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findAllByBookerAndStatus(Long userId, String state) {
        log.info("Find all bookings by booker and status");
        userExist(userId);
        return switch (state) {
            case "ALL" -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case "CURRENT" ->
                    bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDto)
                            .toList();
            case "PAST" ->
                    bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDto)
                            .toList();
            case "FUTURE" ->
                    bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDto)
                            .toList();
            case "WAITING", "REJECTED" ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state)).stream()
                            .map(BookingMapper::toBookingDto)
                            .toList();
            default -> throw new RuntimeException("Unknown state: " + state);
        };
    }

    @Override
    public Collection<BookingDto> findAllByOwnerAndStatus(Long userId, String state) {
        log.info("Find all by owner and status");
        userExist(userId);
        return switch (state) {
            case "ALL" -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case "CURRENT" ->
                    bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDto)
                            .toList();
            case "PAST" ->
                    bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDto)
                            .toList();
            case "FUTURE" ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDto)
                            .toList();
            case "WAITING", "REJECTED" ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state)).stream()
                            .map(BookingMapper::toBookingDto)
                            .toList();
            default -> throw new RuntimeException("Unknown state: " + state);
        };
    }

    private void userExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found!");
        }
    }

    private void validate(Long userId, BookingRequestDto bookingRequestDto) {
        log.info("Validation");

        if (bookingRequestDto.getEnd() == null) {
            throw new ValidationException("End date is null!");
        }

        if (bookingRequestDto.getStart() == null) {
            throw new ValidationException("Start date is null!");
        }

        if (bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("End date before now!");
        }

        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date before now!");
        }

        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new ValidationException("End date before Start date!");
        }

        if (bookingRequestDto.getEnd().equals(bookingRequestDto.getStart())) {
            throw new ValidationException("End date equals Start date!");
        }

        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Item id = " + bookingRequestDto.getItemId() + " not found!"));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Item is already booked!");
        }

        if (!(item.getAvailable())) {
            throw new ValidationException("Available is not true!");
        }

        if (bookingRepository.findAllByItemId(item.getId()).stream()
                .anyMatch(booking -> (booking.getStart().isAfter(bookingRequestDto.getStart())
                        && booking.getStart().isBefore(bookingRequestDto.getEnd()))
                        || (booking.getEnd().isAfter(bookingRequestDto.getStart())
                        && booking.getEnd().isBefore(bookingRequestDto.getEnd())))) {
            throw new ValidationException("Crossing dates!");
        }
    }
}