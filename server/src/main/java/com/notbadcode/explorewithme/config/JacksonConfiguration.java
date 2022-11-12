package com.notbadcode.explorewithme.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfiguration {

    @Value("${spring.jackson.date-format}")
    String format;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            builder.deserializers(new LocalDateTimeDeserializer(formatter));
            builder.serializers(new LocalDateTimeSerializer(formatter));
        };
    }
}
