package org.example.warehouseonline.controller;

import org.example.warehouseonline.repository.WarehouseItemsRepository;
import org.example.warehouseonline.entity.WareHouseItems;
import org.example.warehouseonline.service.Impl.WarehouseItemsServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/add")
    public WareHouseItems addItem(@RequestParam("sku") String sku,
                                  @RequestParam("name") String name,
                                  @RequestParam("quantity") int quantity,
                                  @RequestParam("price") double price,
                                  @RequestParam("category") String category,
                                  @RequestParam("arrivalDate") String arrivalDate,
                                  @RequestParam("supplier") String supplier,
                                  @RequestParam("warehouse") String warehouse,
                                  @RequestParam("fileName") String fileName,
                                  @RequestParam("image") MultipartFile image) {
        return warehouseItemsService.addItem(sku, name, quantity, price, category, arrivalDate, supplier, warehouse, fileName, image);
    }

    @PostMapping("/update")
    public WareHouseItems updateItem(@RequestParam("id") String id,
                                     @RequestParam("sku") String sku,
                                     @RequestParam("name") String name,
                                     @RequestParam("quantity") int quantity,
                                     @RequestParam("price") double price,
                                     @RequestParam("category") String category,
                                     @RequestParam("arrivalDate") String arrivalDate,
                                     @RequestParam("supplier") String supplier,
                                     @RequestParam("warehouse") String warehouse,
                                     @RequestParam("fileName") String fileName,
                                     @RequestParam(value = "image", required = false) MultipartFile image) {
        return warehouseItemsService.updateItem(id, sku, name, quantity, price, category, arrivalDate, supplier, warehouse, fileName, image);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        return warehouseItemsService.deleteProductById(Math.toIntExact(productId));
    }
}
