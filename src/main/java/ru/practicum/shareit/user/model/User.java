package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private String email;
}
