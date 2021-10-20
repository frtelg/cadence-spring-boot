package com.frtelg.cadence.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        mapper.configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false);
        mapper.registerModule(module).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new Jdk8Module());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        return mapper;
    }
}
