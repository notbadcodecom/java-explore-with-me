package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.event.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
