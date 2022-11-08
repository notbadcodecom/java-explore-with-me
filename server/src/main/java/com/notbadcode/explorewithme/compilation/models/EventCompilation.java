package com.notbadcode.explorewithme.compilation.models;

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
@Table(name = "events_compilations", schema = "public")
public class EventCompilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_compilation_id", nullable = false)
    Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "compilation_id", referencedColumnName = "compilation_id")
    Compilation compilation;
}
