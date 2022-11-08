package com.notbadcode.explorewithme.admin.storage;

import com.notbadcode.explorewithme.admin.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminUserRepository extends JpaRepository<User, Long> {
    List<User> findByIdInOrderByIdAsc(List<Long> ids, Pageable pageable);
}
