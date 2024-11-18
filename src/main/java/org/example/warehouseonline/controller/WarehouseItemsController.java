package org.example.warehouseonline.controller;

import org.example.warehouseonline.repository.WarehouseItemsRepository;
import org.example.warehouseonline.entity.WareHouseItems;
import org.example.warehouseonline.service.Impl.WarehouseItemsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/items")
public class WarehouseItemsController {

    private final WarehouseItemsServiceImpl warehouseItemsService;

    @Autowired
    public WarehouseItemsController(WarehouseItemsServiceImpl warehouseItemsService, WarehouseItemsRepository warehouseItemsRepository) {
        this.warehouseItemsService = warehouseItemsService;
    }

    @GetMapping
    public List<WareHouseItems> getAllItems(
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String filter
    ) {
        return warehouseItemsService.getFilteredItems(warehouse, category, filter);
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
                                     @RequestParam(value = "sku") String sku,
                                     @RequestParam(value = "name") String name,
                                     @RequestParam(value = "quantity") int quantity,
                                     @RequestParam(value = "price") double price,
                                     @RequestParam(value = "category") String category,
                                     @RequestParam(value = "arrivalDate") String arrivalDate,
                                     @RequestParam(value = "supplier") String supplier,
                                     @RequestParam(value = "warehouse") String warehouse,
                                     @RequestParam(value = "fileName") String fileName,
                                     @RequestParam(value = "image") MultipartFile image) {
        return warehouseItemsService.updateItem(id, sku, name, quantity, price, category, arrivalDate, supplier, warehouse, fileName, image);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        return warehouseItemsService.deleteProductById(Math.toIntExact(productId));
    }
}
