package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items;
    private Long nextId = 1L;

    @Override
    public Collection<Item> findAll() {
        log.info("Finding all items");
        return items.values();
    }

    @Override
    public Item create(Item item) {
        log.info("Creating new item");
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        log.info("Updating new item");
        Item item = items.get(newItem.getId());
        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        return item;
    }

    @Override
    public Item findItemById(Long itemId) {
        log.info("Finding new item by id: " + itemId);
        return items.get(itemId);
    }

    @Override
    public Collection<Item> findItemsByUserId(Long userId) {
        log.info("Finding new items by userid: " + userId);
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
    }

    @Override
    public void isItemExist(Long itemId) {
        log.info("Checking if item exists with id: " + itemId);
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item id = " + itemId + " не найден!");
        }
    }

    @Override
    public void delete(Long itemId) {
        log.info("Deleting new item with id: " + itemId);
        items.remove(itemId);
    }

    @Override
    public void isOwner(Long userId, Long itemId) {
        log.info("Checking if item owns with id: " + itemId);
        if (!items.get(itemId).getOwner().equals(userId)) {
            throw new NotFoundException("Вы не владелец Item!");
        }
    }

    @Override
    public Collection<Item> findItemsByText(String text) {
        log.info("Finding new items by text: " + text);
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable().equals(true)).toList();
    }
}
