package com.twins.demo_twins.infrastructure.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            @Qualifier("dltKafkaTemplate")
            KafkaTemplate<Object, Object> dltKafkaTemplate,
            ObjectMapper objectMapper) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        dltKafkaTemplate,
                        (record, ex) -> new TopicPartition(
                                record.topic() + "-dlt",
                                record.partition()
                        )
                ) {
                    @Override
                    protected ProducerRecord<Object, Object> createProducerRecord(
                            ConsumerRecord<?, ?> record,
                            TopicPartition topicPartition,
                            Headers headers,
                            byte[] key,
                            byte[] value) {

                        byte[] payload;

                        if (record.value() == null) {
                            payload = value;
                        } else {
                            try {
                                payload = objectMapper.writeValueAsBytes(record.value());
                            } catch (Exception e) {
                                payload = String.valueOf(record.value())
                                        .getBytes(java.nio.charset.StandardCharsets.UTF_8);
                            }
                        }

                        headers.add("content-type", "application/json".getBytes(StandardCharsets.UTF_8));

                        return new ProducerRecord<>(
                                topicPartition.topic(),
                                topicPartition.partition(),
                                null,
                                key,
                                payload,
                                headers
                        );
                    }

                };

        FixedBackOff backOff = new FixedBackOff(0L, 0);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        handler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                SerializationException.class,
                DeserializationException.class
        );

        return handler;
    }
}
