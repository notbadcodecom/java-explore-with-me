package com.notbadcode.explorewithme.admin.service;

import com.notbadcode.explorewithme.admin.dto.UserDto;
import com.notbadcode.explorewithme.admin.dto.UserShortDto;
import com.notbadcode.explorewithme.admin.mapper.UserMapper;
import com.notbadcode.explorewithme.admin.model.User;
import com.notbadcode.explorewithme.admin.storage.AdminUserRepository;
import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.util.FromSizeRequest;
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
public class AdminUserService {
    private final AdminUserRepository userRepository;

    @Transactional
    public UserDto createUser(UserShortDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.debug("User id={} has been created", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserOr404Error(userId);
        userRepository.delete(user);
        log.debug("User id={} has been deleted", userId);
    }

    public List<UserDto> findAllUsers(Optional<List<Long>> ids, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size);
        return ids.map(longs -> userRepository.findByIdInOrderByIdAsc(longs, pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList())).orElseGet(() -> userRepository.findAll(pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()));
    }


    private User getUserOr404Error(Long userId) {
        log.debug("Fi id={} has been deleted", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }
}
