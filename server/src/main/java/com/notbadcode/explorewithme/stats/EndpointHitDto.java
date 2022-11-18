package com.notbadcode.explorewithme.stats;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitDto {
    String app;

    String uri;

    String ip;
}
