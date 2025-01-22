package org.example.warehouseonline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddItemRequestDTO {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @NotBlank(message = "Category is required")
    private String category;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Arrival date must be in the format yyyy-MM-dd")
    private String arrivalDate;

    @NotBlank(message = "Supplier is required")
    private String supplier;

    @NotBlank(message = "Warehouse is required")
    private String warehouse;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotNull(message = "Image is required")
    private MultipartFile image;
}
