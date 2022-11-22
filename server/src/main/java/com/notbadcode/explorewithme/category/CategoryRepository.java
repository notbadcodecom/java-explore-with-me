package com.notbadcode.explorewithme.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<EventCategory, Long> {
    @Query(value = "SELECT count(e.event_id) > 0 FROM events e WHERE e.category_id = :id",
            nativeQuery = true)
    boolean existsEventsByCategoryId(Long id);
}
