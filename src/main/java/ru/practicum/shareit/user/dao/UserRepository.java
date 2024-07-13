package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    User findUserById(Long userId);

    void isUserEmailExist(String email);

    void isUserExist(Long userId);

    void delete(Long userId);

}
