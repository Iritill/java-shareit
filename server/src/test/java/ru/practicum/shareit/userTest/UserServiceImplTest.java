package ru.practicum.shareit.userTest;

import ru.practicum.shareit.config.KafkaSender;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private User user;
    private UserDto userDto;
    @Autowired
    private KafkaSender kafkaSender;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        kafkaSender = mock(KafkaSender.class);
        userService = new UserServiceImpl(userRepository, kafkaSender);
        user = User.builder()
                .id(1L)
                .name("TestUserName")
                .email("TestUserEmail@test.com")
                .build();
        userDto = UserMapper.toUserDto(user);

        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
    }

    @Test
    void findAll() {
        List<UserDto> result = userService.findAll().stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getId(), userDto.getId());
        Assertions.assertEquals(result.getFirst().getName(), userDto.getName());
        Assertions.assertEquals(result.getFirst().getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void create() {
        UserDto result = userService.create(userDto);
        validate(result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void update() {
        UserDto result = userService.update(user.getId(), userDto);
        validate(result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUserDtoById() {
        UserDto result = userService.getUserDtoById(user.getId());
        validate(result);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void delete() {
        userService.delete(user.getId());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    void validate(UserDto result) {
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), userDto.getId());
        Assertions.assertEquals(result.getName(), userDto.getName());
        Assertions.assertEquals(result.getEmail(), userDto.getEmail());
    }

}
