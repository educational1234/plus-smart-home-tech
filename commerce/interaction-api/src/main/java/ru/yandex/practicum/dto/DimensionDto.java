package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {

    @NotNull
    @DecimalMin(value = "1", message = "Ширина товара не может быть меньше 1")
    Double width;

    @NotNull
    @DecimalMin(value = "1", message = "Высота товара не может быть меньше 1")
    Double height;

    @NotNull
    @DecimalMin(value = "1", message = "Глубина товара не может быть меньше 1")
    Double depth;
}