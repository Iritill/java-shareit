package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dao.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Create new item");
        return ItemMapper.toItemDto(itemRepository.save(
                Item.builder()
                        .name(itemDto.getName())
                        .owner(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User id = " + userId + " not found!")))
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .build()
        ));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        log.info("Add comment");
        if (commentRequestDto.getText().isBlank() || commentRequestDto.getText().isEmpty()) {
            throw new ValidationException("Comment is empty!");
        }
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("The user not book this item");
        }
        return CommentMapper.toCommentDto(commentRepository.save(Comment.builder()
                .author(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User id " + userId + " not found!")))
                .item(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item id " + itemId + " not found!")))
                .text(commentRequestDto.getText())
                .created(LocalDateTime.now())
                .build()));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Update item");
        validation(userId, itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item id " + itemId + " not found!"));
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemInfoDto findItemById(Long userId, Long itemId) {
        log.info("Find item by id");
        return ItemMapper.toItemInfoDto(
                itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item (id " + itemId + ") not found!")),
                BookingMapper.toBookingDateInfoDto(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED).orElse(null)),
                BookingMapper.toBookingDateInfoDto(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED).orElse(null)),
                CommentMapper.toCommentsDtoCollection(commentRepository.findAllByItemId(itemId))
        );
    }

    @Override
    public Collection<ItemInfoDto> findItemsByUserId(Long userId) {
        log.info("Find items by user id");
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId).stream()
                .map(item -> ItemMapper.toItemInfoDto(item,
                        BookingMapper.toBookingDateInfoDto(item.getBookings().isEmpty() ? null : item.getBookings().getFirst()),
                        BookingMapper.toBookingDateInfoDto(item.getBookings().isEmpty() ? null : item.getBookings().getLast()),
                        CommentMapper.toCommentsDtoCollection(item.getComments())))
                .toList();
    }

    @Override
    public Collection<ItemDto> findItemsByText(String text) {
        log.info("Find items by text");
        if (text.isBlank() || text.isEmpty()) return List.of();
        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text).stream().map(ItemMapper::toItemDto).toList();
    }

    private void validation(Long userId, Long itemId) {
        if (userId == null) {
            throw new ValidationException("Owner id not specified!");
        }
        if (itemId != null && !(Objects.equals(Objects.requireNonNull(itemRepository.findById(itemId).orElse(null)).getOwner().getId(), userId))) {
            throw new NotFoundException("Only the owner can edit an item!");
        }
        if (itemId != null && !itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item not found!");
        }
    }

}
