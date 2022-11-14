package com.notbadcode.explorewithme.user;

import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.user.dto.UserDto;
import com.notbadcode.explorewithme.user.dto.UserShortDto;
import com.notbadcode.explorewithme.util.SizeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(UserShortDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.debug("User id={} has been created", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.delete(getUserOr404Error(userId));
        log.debug("User id={} has been deleted", userId);
    }

    public List<UserDto> findUsers(Optional<List<Long>> ids, int from, int size) {
        Pageable pageable = SizeRequest.from(from, size);
        List<UserDto> users = ids.map(longs -> userRepository.findByIdInOrderByIdAsc(longs, pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList())).orElseGet(() -> userRepository.findAll(pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()));
        log.debug("Found {} users", users.size());
        return users;
    }

    public User getUserOr404Error(Long userId) {
        log.debug("Load user id={}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }
}
