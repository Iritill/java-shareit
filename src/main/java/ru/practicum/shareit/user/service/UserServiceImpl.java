package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        log.info("Find all users");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Create user {}", userDto);
        return UserMapper.toUserDto(userRepository.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Update user {}", userDto);
        userDto.setId(userId);
        return UserMapper.toUserDto(userRepository.update(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        log.info("Get user {}", userId);
        return UserMapper.toUserDto(userRepository.findUserById(userId));
    }

    @Override
    public void delete(Long userId) {
        log.info("Delete user {}", userId);
        userRepository.delete(userId);
    }
}
