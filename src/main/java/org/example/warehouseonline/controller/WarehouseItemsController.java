package org.example.warehouseonline.controller;

import org.springframework.core.io.Resource;
import org.example.warehouseonline.entity.WareHouseItems;
import org.example.warehouseonline.service.Impl.WarehouseItemsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/warehouse/items")
public class WarehouseItemsController {

    private final WarehouseItemsServiceImpl warehouseItemsService;

    private ResourceLoader resourceLoader;

    @Autowired
    public WarehouseItemsController(WarehouseItemsServiceImpl warehouseItemsService) {
        this.warehouseItemsService = warehouseItemsService;
    }

    @Autowired
    public void ImageController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping
    public List<WareHouseItems> getAllItems(
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String filter
    ) {
        return warehouseItemsService.getFilteredItems(warehouse, category, filter);
    }

    @GetMapping("/images/{imageName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        Path imagePath = Paths.get("D:\\IdeaProjects\\WarehouseOnline\\src\\main\\resources\\static\\images\\products")
                .resolve(imageName);

        if (Files.exists(imagePath) && Files.isReadable(imagePath)) {
            Resource resource = resourceLoader.getResource("file:" + imagePath.toString());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
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
                                  @RequestParam("image") MultipartFile image) {
        return warehouseItemsService.addItem(sku, name, quantity, price, category, arrivalDate, supplier, warehouse, image);
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
                                    @RequestParam("warehouse") String warehouse) {
        return warehouseItemsService.updateItem(id, sku, name, quantity, price, category, arrivalDate, supplier, warehouse);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        warehouseItemsService.deleteProductById(Math.toIntExact(productId));
        return new ResponseEntity<>("Product with ID: " + productId + " has been deleted", HttpStatus.OK);
    }
}
