package org.example.warehouseonline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "warehouse_order")
public class WarehouseOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Уникальный идентификатор заказа, который помогает однозначно идентифицировать каждый товар.
    private String orderId;

    private byte[] image;

    private String fileName;

    private String name;

    private Integer quantity;

    private double totalAmount;

    private String category;

    private String warehouse;

    //Дата размещения заказа.
    private LocalDate OrderDate;

    //Планируемая дата доставки заказа.
    private LocalDate deliveryDate;

    private String orderStatus;
}
