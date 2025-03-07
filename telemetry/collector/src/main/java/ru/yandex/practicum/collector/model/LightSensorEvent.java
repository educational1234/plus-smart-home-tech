package ru.yandex.practicum.collector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class LightSensorEvent extends SensorEvent {
    private int linkQuality;
    private int luminosity;
}
