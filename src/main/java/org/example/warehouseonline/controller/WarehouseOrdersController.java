package org.example.warehouseonline.controller;


import org.example.warehouseonline.entity.WarehouseOrders;
import org.example.warehouseonline.service.Impl.WarehouseOrdersServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
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
    public WarehouseOrders addOrder(@RequestParam("orderId") String orderId,
                                  @RequestParam("name") String name,
                                  @RequestParam("quantity") int quantity,
                                  @RequestParam("totalAmount") double totalAmount,
                                  @RequestParam("category") String category,
                                  @RequestParam("orderDate") String OrderDate,
                                  @RequestParam("deliveryDate") String deliveryDate,
                                  @RequestParam("warehouse") String warehouse,
                                  @RequestParam("fileName") String fileName,
                                  @RequestParam("orderStatus") String orderStatus,
                                  @RequestParam("image") MultipartFile image) {
        return warehouseOrdersService.addOrder(orderId, name, quantity, totalAmount, category, OrderDate, deliveryDate, warehouse, orderStatus, fileName, image);
    }

    @PostMapping("/update")
    @CrossOrigin(origins = "http://localhost:3000")
    public WarehouseOrders updateOrder(@RequestParam("id") String id,
                                     @RequestParam("orderId") String orderId,
                                     @RequestParam("name") String name,
                                     @RequestParam("quantity") int quantity,
                                     @RequestParam("totalAmount") double totalAmount,
                                     @RequestParam("category") String category,
                                     @RequestParam("orderDate") String orderDate,
                                     @RequestParam("deliveryDate") String deliveryDate,
                                      @RequestParam("orderStatus") String orderStatus,
                                     @RequestParam("warehouse") String warehouse) {
        return warehouseOrdersService.updateOrder(id, orderId, name, quantity, totalAmount, category, orderDate, deliveryDate, orderStatus, warehouse);
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        return warehouseOrdersService.deleteOrderById(Math.toIntExact(orderId));
    }
}
