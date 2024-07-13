package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        return UserMapper.toUserDtoCollection(userRepository.findAll());
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        userDto.setId(userId);
        return UserMapper.toUserDto(userRepository.update(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        return UserMapper.toUserDto(userRepository.findUserById(userId));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }
}
