package org.example.warehouseonline.controller;

import jakarta.validation.Valid;
import org.example.warehouseonline.dto.AddItemRequestDTO;
import org.example.warehouseonline.dto.UpdateItemRequestDTO;
import org.example.warehouseonline.entity.WareHouseItems;
import org.example.warehouseonline.service.Impl.WarehouseItemsServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse/items")
public class WarehouseItemsController {

    private final WarehouseItemsServiceImpl warehouseItemsService;

    @Autowired
    public WarehouseItemsController(WarehouseItemsServiceImpl warehouseItemsService) {
        this.warehouseItemsService = warehouseItemsService;
    }

    @GetMapping
    public ResponseEntity<Page<WareHouseItems>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String filter) {
        return warehouseItemsService.getFilteredItems(page, size, warehouse, category, filter);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return warehouseItemsService.getImage(id);
    }

    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WareHouseItems addItem(@ModelAttribute @Valid AddItemRequestDTO addItemRequestDTO) {
        return warehouseItemsService.addItem(addItemRequestDTO.getSku(),
                addItemRequestDTO.getName(),
                addItemRequestDTO.getQuantity(),
                addItemRequestDTO.getPrice(),
                addItemRequestDTO.getCategory(),
                addItemRequestDTO.getArrivalDate(),
                addItemRequestDTO.getSupplier(),
                addItemRequestDTO.getWarehouse(),
                addItemRequestDTO.getFileName(),
                addItemRequestDTO.getImage());
    }

    @PostMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WareHouseItems updateItem(@ModelAttribute @Valid UpdateItemRequestDTO updateItemRequestDTO) {
        return warehouseItemsService.updateItem(updateItemRequestDTO.getId(),
                updateItemRequestDTO.getSku(),
                updateItemRequestDTO.getName(),
                updateItemRequestDTO.getQuantity(),
                updateItemRequestDTO.getPrice(),
                updateItemRequestDTO.getCategory(),
                updateItemRequestDTO.getArrivalDate(),
                updateItemRequestDTO.getSupplier(),
                updateItemRequestDTO.getWarehouse(),
                updateItemRequestDTO.getFileName(),
                updateItemRequestDTO.getImage());
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        return warehouseItemsService.deleteProductById(Math.toIntExact(productId));
    }
}
