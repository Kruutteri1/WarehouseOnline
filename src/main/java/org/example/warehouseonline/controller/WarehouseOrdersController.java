package org.example.warehouseonline.controller;


import org.example.warehouseonline.entity.WarehouseOrders;
import org.example.warehouseonline.service.Impl.WarehouseOrdersServiceImpl;
import org.springframework.core.io.Resource;

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
@RequestMapping("/api/warehouse/orders")
public class WarehouseOrdersController {

    private final WarehouseOrdersServiceImpl warehouseOrdersService;

    private ResourceLoader resourceLoader;

    @Autowired
    public WarehouseOrdersController(WarehouseOrdersServiceImpl warehouseOrdersService) {
        this.warehouseOrdersService = warehouseOrdersService;
    }

    @Autowired
    public void ImageController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping
    public List<WarehouseOrders> getAllItems(
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String filter
    ) {
        return warehouseOrdersService.getFilteredOrders(warehouse, category, filter);
    }

    @GetMapping("/images/{imageName:.+}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        Path imagePath = Paths.get("D:\\IdeaProjects\\WarehouseOnline\\src\\main\\resources\\static\\images\\orders")
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
    @CrossOrigin(origins = "http://localhost:3000")
    public WarehouseOrders addItem(@RequestParam("order_id") String order_id,
                                  @RequestParam("name") String name,
                                  @RequestParam("quantity") int quantity,
                                  @RequestParam("total_amount") double total_amount,
                                  @RequestParam("category") String category,
                                  @RequestParam("orderDate") String OrderDate,
                                  @RequestParam("delivery_date") String delivery_date,
                                  @RequestParam("warehouse") String warehouse,
                                   @RequestParam("order_status") String order_status,
                                  @RequestParam("image") MultipartFile image) {
        return warehouseOrdersService.addItem(order_id, name, quantity, total_amount, category, OrderDate, delivery_date, warehouse, order_status, image);
    }

    @PostMapping("/update")
    @CrossOrigin(origins = "http://localhost:3000")
    public WarehouseOrders updateItem(@RequestParam("id") String id,
                                     @RequestParam("order_id") String order_id,
                                     @RequestParam("name") String name,
                                     @RequestParam("quantity") int quantity,
                                     @RequestParam("total_amount") double total_amount,
                                     @RequestParam("category") String category,
                                     @RequestParam("orderDate") String orderDate,
                                     @RequestParam("delivery_date") String delivery_date,
                                      @RequestParam("order_status") String order_status,
                                     @RequestParam("warehouse") String warehouse) {
        return warehouseOrdersService.updateOrder(id, order_id, name, quantity, total_amount, category, orderDate, delivery_date, order_status, warehouse);
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        warehouseOrdersService.deleteOrderById(Math.toIntExact(orderId));
        return new ResponseEntity<>("Order with ID: " + orderId + " has been deleted", HttpStatus.OK);
    }
}
