package com.notbadcode.explorewithme.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRequestParticipation is a Querydsl query type for RequestParticipation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRequestParticipation extends EntityPathBase<RequestParticipation> {

    private static final long serialVersionUID = 1631159990L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRequestParticipation requestParticipation = new QRequestParticipation("requestParticipation");

    public final BooleanPath confirmed = createBoolean("confirmed");

    public final com.notbadcode.explorewithme.event.model.QEvent event;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUser participant;

    public QRequestParticipation(String variable) {
        this(RequestParticipation.class, forVariable(variable), INITS);
    }

    public QRequestParticipation(Path<? extends RequestParticipation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRequestParticipation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRequestParticipation(PathMetadata metadata, PathInits inits) {
        this(RequestParticipation.class, metadata, inits);
    }

    public QRequestParticipation(Class<? extends RequestParticipation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new com.notbadcode.explorewithme.event.model.QEvent(forProperty("event"), inits.get("event")) : null;
        this.participant = inits.isInitialized("participant") ? new QUser(forProperty("participant")) : null;
    }

}

