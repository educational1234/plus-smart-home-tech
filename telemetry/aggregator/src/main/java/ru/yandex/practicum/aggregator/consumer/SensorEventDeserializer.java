package ru.yandex.practicum.aggregator.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Map;

@Slf4j
public class SensorEventDeserializer implements Deserializer<SensorEventAvro> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public SensorEventAvro deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        SpecificDatumReader<SensorEventAvro> reader = new SpecificDatumReader<>(SensorEventAvro.getClassSchema());
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        try {
            return reader.read(null, decoder);
        } catch (Exception e) {
            log.error("Ошибка десериализации SensorEventAvro", e);
            return null;
        }
    }

    @Override
    public void close() {
    }
}
