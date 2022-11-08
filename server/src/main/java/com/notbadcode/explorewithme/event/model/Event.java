package com.notbadcode.explorewithme.event.model;


import com.notbadcode.explorewithme.category.EventCategory;
import com.notbadcode.explorewithme.admin.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long id;

    @Column(name = "annotation", nullable = false)
    String annotation;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", nullable = false)
    String description;

    @Builder.Default
    @Column(name = "state", nullable = false)
    EventState state = EventState.PENDING;

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    LocalDateTime createdOn;

    @Column(name = "published")
    LocalDateTime publishedOn;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "user_id")
    User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    Location location;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    EventCategory category;

    @Column(name = "paid")
    Boolean paid;

    @Column(name = "moderation")
    Boolean moderation;

    @Column(name = "participant_limit")
    Boolean participantLimit;
}
