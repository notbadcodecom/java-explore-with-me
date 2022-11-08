package com.notbadcode.explorewithme.admin.model;

import com.notbadcode.explorewithme.event.model.Event;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requests_participation", schema = "public")
public class RequestParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    User participant;

    @Column(name = "confirmed")
    Boolean confirmed;
}
