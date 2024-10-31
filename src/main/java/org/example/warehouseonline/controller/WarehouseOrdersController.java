package org.example.warehouseonline.controller;


import org.example.warehouseonline.entity.WarehouseOrders;
import org.example.warehouseonline.service.Impl.WarehouseOrdersServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/orders")
public class WarehouseOrdersController {

    private final WarehouseOrdersServiceImpl warehouseOrdersService;

    @Autowired
    public WarehouseOrdersController(WarehouseOrdersServiceImpl warehouseOrdersService) {
        this.warehouseOrdersService = warehouseOrdersService;
    }

    @GetMapping
    public List<WarehouseOrders> getAllOrders(
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String filter
    ) {
        return warehouseOrdersService.getFilteredOrders(warehouse, category, filter);
    }

    @GetMapping("/oderImage/{id}")
    public ResponseEntity<byte[]> getOrderImage(@PathVariable Long id) {
        return warehouseOrdersService.getOrderImage(id);
    }

    @PostMapping("/add")
    @CrossOrigin(origins = "http://localhost:3000")
    public WarehouseOrders addOrder(@RequestParam("order_id") String order_id,
                                  @RequestParam("name") String name,
                                  @RequestParam("quantity") int quantity,
                                  @RequestParam("total_amount") double total_amount,
                                  @RequestParam("category") String category,
                                  @RequestParam("orderDate") String OrderDate,
                                  @RequestParam("delivery_date") String delivery_date,
                                  @RequestParam("warehouse") String warehouse,
                                  @RequestParam("fileName") String fileName,
                                  @RequestParam("order_status") String order_status,
                                  @RequestParam("image") MultipartFile image) {
        return warehouseOrdersService.addOrder(order_id, name, quantity, total_amount, category, OrderDate, delivery_date, warehouse, order_status, fileName, image);
    }

    @PostMapping("/update")
    //@CrossOrigin(origins = "http://localhost:3000")
    public WarehouseOrders updateOrder(@RequestParam("id") String id,
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
        return warehouseOrdersService.deleteOrderById(Math.toIntExact(orderId));
    }
}
