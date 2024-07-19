package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Item {
    private Long id;
    private String name;
    private String description;
    private Long owner;
    private ItemRequest request;
    private Boolean available;
}
