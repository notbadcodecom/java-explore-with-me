package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.event.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>  {
    boolean existsByCategory_Id(Long id);

    List<Event> findByInitiator_IdOrderByEventDateDesc(Long id, Pageable pageable);

    List<Event> findByIdIn(List<Long> ids);
}
