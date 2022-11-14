package com.notbadcode.explorewithme.participation;

import com.notbadcode.explorewithme.event.model.Event;
import com.notbadcode.explorewithme.user.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requests_participation", schema = "public")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id", nullable = false)
    Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    User participant;

    @Setter
    @Builder.Default
    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    ParticipationStatus status = ParticipationStatus.PENDING;

    @Column(name = "created")
    LocalDateTime created;
}
