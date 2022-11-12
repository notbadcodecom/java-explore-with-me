package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.event.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
