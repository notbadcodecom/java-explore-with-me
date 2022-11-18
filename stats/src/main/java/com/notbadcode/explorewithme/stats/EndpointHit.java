package com.notbadcode.explorewithme.stats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Schema
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "endpoint_hit", schema = "public")
public class EndpointHit {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")

    Long id;

    @NotBlank
    @Column(name = "app")
    String app;

    @NotBlank
    @Column(name = "uri")
    String uri;

    @NotBlank
    @Column(name = "ip")
    String ip;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    LocalDateTime timestamp;
}
