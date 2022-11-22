package com.notbadcode.explorewithme.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.notbadcode.explorewithme.util.CommonDateTime;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.deserializers(new LocalDateTimeDeserializer(CommonDateTime.getFormatter()));
            builder.serializers(new LocalDateTimeSerializer(CommonDateTime.getFormatter()));
        };
    }
}
