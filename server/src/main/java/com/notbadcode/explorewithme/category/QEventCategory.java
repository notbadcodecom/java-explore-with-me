package com.notbadcode.explorewithme.category;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEventCategory is a Querydsl query type for EventCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventCategory extends EntityPathBase<EventCategory> {

    private static final long serialVersionUID = -2072070425L;

    public static final QEventCategory eventCategory = new QEventCategory("eventCategory");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QEventCategory(String variable) {
        super(EventCategory.class, forVariable(variable));
    }

    public QEventCategory(Path<? extends EventCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEventCategory(PathMetadata metadata) {
        super(EventCategory.class, metadata);
    }

}

