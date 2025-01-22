package org.example.warehouseonline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddOrderRequestDTO {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than 0")
    private Double totalAmount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Order date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Order date must be in the format yyyy-MM-dd")
    private String orderDate;

    @NotBlank(message = "Delivery date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Delivery date must be in the format yyyy-MM-dd")
    private String deliveryDate;

    @NotBlank(message = "Order status is required")
    private String orderStatus;

    @NotBlank(message = "Warehouse is required")
    private String warehouse;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotNull(message = "Image is required")
    private MultipartFile image;
}
