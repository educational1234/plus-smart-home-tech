package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductState;
import ru.yandex.practicum.dto.QuantityState;

import java.util.UUID;

@Entity
@Data
@Table(name = "products", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    UUID productId;

    @Column(name = "name")
    private String productName;

    @Column(name = "description")
    private String description;

    @Column(name = "image_src")
    private String imageSrc;

    @Column(name = "quantity_state")
    private QuantityState quantityState;

    @Column(name = "product_state")
    private ProductState productState;

    @Column(name = "product_category")
    private ProductCategory productCategory;

    @Column(name = "price")
    private Double price;
}
