package com.notbadcode.explorewithme.user;

import com.notbadcode.explorewithme.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIdInOrderByIdAsc(List<Long> ids, Pageable pageable);
}
