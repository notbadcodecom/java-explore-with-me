package ru.practicum.explorewithme.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<EventCategory, Long> {
}
