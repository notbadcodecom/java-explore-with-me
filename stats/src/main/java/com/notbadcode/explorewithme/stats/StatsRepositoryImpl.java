package com.notbadcode.explorewithme.stats;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {
    private final EntityManager entityManager;
    @Value("${spring.jackson.date-format}") private String format;

    @Override
    @Transactional
    public EndpointHit save(EndpointHit hit) {
        entityManager.persist(hit);
        return hit;
    }

    @Override
    public List<ViewStats> findStats(
            String start,
            String end,
            Optional<List<String>> urisOptional,
            boolean unique
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);
        BooleanBuilder builder = new BooleanBuilder();
        QEndpointHit hit = QEndpointHit.endpointHit;
        builder.and(hit.timestamp.between(startDateTime, endDateTime));
        urisOptional.ifPresent(uris -> builder.and(hit.uri.in(uris)));
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        NumberPath<Long> hits = Expressions.numberPath(Long.class, "hits");
        NumberExpression<Long> count = (unique) ? hit.ip.countDistinct().as(hits) : hit.ip.count().as(hits);
        return queryFactory.select(
                        Projections.constructor(ViewStats.class,
                                hit.app, hit.uri, count))
                .from(hit)
                .where(builder)
                .groupBy(hit.app)
                .groupBy(hit.uri)
                .orderBy(hits.desc())
                .fetch();
    }
}
