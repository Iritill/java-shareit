package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private Long nextID = 1L;
    private final Map<Long, User> users;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        isUserEmailExist(user.getEmail());
        user.setId(nextID++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        isUserExist(userId);
        users.remove(userId);
    }

    @Override
    public User update(User newUser) {
        isUserExist(newUser.getId());
        User user = users.get(newUser.getId());
        if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            isUserEmailExist(newUser.getEmail());
            user.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        isUserExist(userId);
        return users.get(userId);
    }

    @Override
    public void isUserEmailExist(String email) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new AlreadyExistsException("Email " + email + " занят!");
        }
    }

    @Override
    public void isUserExist(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User id = " + userId + " не найден!");
        }
    }
}
