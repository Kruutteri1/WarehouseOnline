package org.example.warehouseonline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "warehouse_item")
public class WareHouseItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Код товара (SKU): Уникальный идентификатор товара, который помогает однозначно идентифицировать каждый товар.
    private String sku;

    private String name;

    private String fileName;

    private byte[] image;

    private Double price;

    private Integer quantity;

    //Категория товара:
    private String category;

    //Склад
    private String warehouse;

    //Дата поступления на склад:
    private LocalDate arrivalDate;

    //Дата последнего обновления информации:
    private LocalDateTime lastUpdated;

    //Поставщик
    private String supplier;
}
