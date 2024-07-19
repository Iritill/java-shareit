package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> findAll() {
        log.info("Find all items");
        return itemRepository.findAll().stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Create new item");
        validation(userId, null);
        return ItemMapper.toItemDto(itemRepository.create(
                Item.builder()
                        .name(itemDto.getName())
                        .owner(userId)
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .request(null)
                        .build()
        ));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Update item");
        validation(userId, itemId);
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemRepository.update(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        log.info("Find item by id");
        return ItemMapper.toItemDto(itemRepository.findItemById(itemId));
    }

    @Override
    public Collection<ItemDto> findItemsByUserId(Long userId) {
        log.info("Find items by id");
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public void delete(Long itemId) {
        log.info("Delete item");
        itemRepository.delete(itemId);
    }

    @Override
    public Collection<ItemDto> findItemsByText(String text) {
        log.info("Find items by text");
        if (text.isBlank() || text.isEmpty()) return List.of();
        return itemRepository.findItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private void validation(Long userId, Long itemId) {
        log.info("Validate item");
        if (userId == null) {
            throw new ValidationException("Id владельца не указано");
        }
        userRepository.isUserExist(userId);
        if (itemId != null) {
            itemRepository.isItemExist(itemId);
            itemRepository.isOwner(userId, itemId);
        }
    }
}
