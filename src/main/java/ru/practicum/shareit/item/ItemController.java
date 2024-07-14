package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find all items");
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDto(@PathVariable Long itemId) {
        log.info("Get item dto");
        return itemService.findItemById(itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Create item");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Update item");
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemDto(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "", required = false) String text) {
        log.info("Search items");
        return itemService.findItemsByText(text);
    }
}
