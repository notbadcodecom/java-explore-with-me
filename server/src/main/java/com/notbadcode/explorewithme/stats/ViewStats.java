package com.notbadcode.explorewithme.stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStats {
    String app;

    String uri;

    Long hits;
}
