package org.example.warehouseonline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateOrderRequestDTO {

    @NotBlank(message = "Order id is required")
    private String id;

    private String orderId;

    private String name;

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    @Positive(message = "Total amount must be greater than 0")
    private Double totalAmount;

    private String category;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Order date must be in the format yyyy-MM-dd")
    private String orderDate;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Delivery date must be in the format yyyy-MM-dd")
    private String deliveryDate;

    private String orderStatus;

    private String warehouse;

    private String fileName;

    private MultipartFile image;
}
