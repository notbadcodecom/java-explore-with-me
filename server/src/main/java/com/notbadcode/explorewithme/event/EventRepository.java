package com.notbadcode.explorewithme.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findByInitiator_IdOrderByEventDateDesc(Long id, Pageable pageable);

    List<Event> findByIdIn(List<Long> ids);

    @Query(value = "SELECT e FROM Event e " +
            "WHERE distance(e.lat, e.lon, :lat, :lon) <= :radius " +
            "AND e.state = 'PUBLISHED'")
    List<Event> findIncludedInLocation(Double lat, Double lon, Long radius);
}
