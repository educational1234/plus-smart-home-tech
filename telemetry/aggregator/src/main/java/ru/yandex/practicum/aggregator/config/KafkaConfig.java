package ru.yandex.practicum.aggregator.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.serializer.GeneralAvroSerializer;


import java.time.Duration;
import java.util.Properties;

@Configuration

public class KafkaConfig {

    @Value(value = "${aggregator.kafka.closeClientTimeoutSec}")
    private Integer closeTimeoutSeconds;

    @Value(value = "${kafkaServer}")
    private String bootstrapAddress;

    @Value(value = "${aggregator.kafka.producer.id}")
    private String producerId;

    @Value(value = "${aggregator.kafka.consumer.id}")
    private String consumerId;

    @Value(value = "${aggregator.kafka.consumer.group}")
    private String consumerGroupId;

    @Bean
    KafkaConnector getClient() {
        return new KafkaConnector() {

            private Producer<String, SpecificRecordBase> producer;
            private Consumer<String, SensorEventAvro> consumer;

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            @Override
            public Consumer<String, SensorEventAvro> getConsumer() {
                if (consumer == null) {
                    initConsumer();
                }
                return consumer;
            }

            public void initProducer() {
                Properties properties = new Properties();
                properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
                properties.put(ProducerConfig.CLIENT_ID_CONFIG, producerId);
                properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);
                producer = new KafkaProducer<>(properties);
            }

            public void initConsumer() {
                Properties properties = new Properties();
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
                properties.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerId);
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
                consumer = new KafkaConsumer<>(properties);
            }

            @Override
            public void stop() {
                if (producer != null) {
                    producer.flush();
                    producer.close(Duration.ofSeconds(closeTimeoutSeconds));
                }
                if (consumer != null) {
                    consumer.commitSync();
                    consumer.close(Duration.ofSeconds(closeTimeoutSeconds));
                }
            }

        };
    }
}