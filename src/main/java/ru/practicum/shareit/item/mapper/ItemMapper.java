package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDateInfoDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
       if (item == null) return null;
       return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemInfoDto toItemInfoDto(Item item, BookingDateInfoDto lastBooking, BookingDateInfoDto nextBooking, Collection<CommentDto> commentDto) {
        if (item == null) return null;
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentDto)
                .build();
    }
}
