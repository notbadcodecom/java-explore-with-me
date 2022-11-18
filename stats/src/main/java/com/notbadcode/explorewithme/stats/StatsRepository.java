package com.notbadcode.explorewithme.stats;

import java.util.List;
import java.util.Optional;

public interface StatsRepository {
    EndpointHit save(EndpointHit hit);

    List<ViewStats> findStats(
            String start,
            String end,
            Optional<List<String>> urisOptional,
            boolean unique
    );
}
