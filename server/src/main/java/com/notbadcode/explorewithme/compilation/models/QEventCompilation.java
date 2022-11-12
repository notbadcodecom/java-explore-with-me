package com.notbadcode.explorewithme.compilation.models;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEventCompilation is a Querydsl query type for EventCompilation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventCompilation extends EntityPathBase<EventCompilation> {

    private static final long serialVersionUID = -1184402717L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEventCompilation eventCompilation = new QEventCompilation("eventCompilation");

    public final QCompilation compilation;

    public final com.notbadcode.explorewithme.event.model.QEvent event;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QEventCompilation(String variable) {
        this(EventCompilation.class, forVariable(variable), INITS);
    }

    public QEventCompilation(Path<? extends EventCompilation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEventCompilation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEventCompilation(PathMetadata metadata, PathInits inits) {
        this(EventCompilation.class, metadata, inits);
    }

    public QEventCompilation(Class<? extends EventCompilation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.compilation = inits.isInitialized("compilation") ? new QCompilation(forProperty("compilation")) : null;
        this.event = inits.isInitialized("event") ? new com.notbadcode.explorewithme.event.model.QEvent(forProperty("event"), inits.get("event")) : null;
    }

}

