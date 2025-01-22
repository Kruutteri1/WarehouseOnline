package org.example.warehouseonline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateItemRequestDTO {

    @NotBlank(message = "Item id is required")
    private String id;

    private String sku;

    private String name;

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    private String category;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Arrival date must be in the format yyyy-MM-dd")
    private String arrivalDate;

    private String supplier;

    private String warehouse;

    private String fileName;

    private MultipartFile image;

}
