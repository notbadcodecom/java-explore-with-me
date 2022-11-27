package com.notbadcode.explorewithme.locations;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "locations", schema = "public")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "location_type", nullable = false)
    @Enumerated(EnumType.STRING)
    LocationType type;

    @Column(name = "lat", nullable = false)
    Double lat;

    @Column(name = "lon", nullable = false)
    Double lon;

    @Column(name = "radius", nullable = false)
    Long radius;
}
