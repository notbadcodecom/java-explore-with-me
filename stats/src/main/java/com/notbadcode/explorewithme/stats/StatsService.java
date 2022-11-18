package com.notbadcode.explorewithme.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository repository;

    @Transactional
    public void saveHit(EndpointHit hit) {
        EndpointHit savedHit = repository.save(hit);
        log.debug("Saved hit: {}", savedHit);
    }

    public List<ViewStats> getStats(
            String start,
            String end,
            Optional<List<String>> uris,
            boolean unique
    ) {
        List<ViewStats> viewStats = repository.findStats(decode(start), decode(end), uris, unique);
        log.debug("Found {} hits", viewStats.size());
        return viewStats;
    }

    private String decode(String text) {
        return URLDecoder.decode(text, UTF_8);
    }
}
