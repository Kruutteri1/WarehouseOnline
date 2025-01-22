package org.example.warehouseonline.controller;

import jakarta.validation.Valid;
import org.example.warehouseonline.dto.AddOrderRequestDTO;
import org.example.warehouseonline.dto.UpdateOrderRequestDTO;
import org.example.warehouseonline.entity.WarehouseOrders;
import org.example.warehouseonline.service.Impl.WarehouseOrdersServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/warehouse/orders")
public class WarehouseOrdersController {

    private final WarehouseOrdersServiceImpl warehouseOrdersService;

    @Autowired
    public WarehouseOrdersController(WarehouseOrdersServiceImpl warehouseOrdersService) {
        this.warehouseOrdersService = warehouseOrdersService;
    }

    @GetMapping
    public ResponseEntity<Page<WarehouseOrders>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String filter
    ) {
        return warehouseOrdersService.getFilteredOrders(page, size, warehouse, category, status, filter);
    }

    @GetMapping("/oderImage/{id}")
    public ResponseEntity<byte[]> getOrderImage(@PathVariable Long id) {
        return warehouseOrdersService.getOrderImage(id);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WarehouseOrders addOrder(@ModelAttribute @Valid AddOrderRequestDTO addOrderRequestDTO) {
        return warehouseOrdersService.addOrder(addOrderRequestDTO.getOrderId(),
                addOrderRequestDTO.getName(),
                addOrderRequestDTO.getQuantity(),
                addOrderRequestDTO.getTotalAmount(),
                addOrderRequestDTO.getCategory(),
                addOrderRequestDTO.getOrderDate(),
                addOrderRequestDTO.getDeliveryDate(),
                addOrderRequestDTO.getWarehouse(),
                addOrderRequestDTO.getOrderStatus(),
                addOrderRequestDTO.getFileName(),
                addOrderRequestDTO.getImage());
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WarehouseOrders updateOrder(@ModelAttribute @Valid UpdateOrderRequestDTO updateOrderRequestDTO) {
        return warehouseOrdersService.updateOrder(updateOrderRequestDTO.getId(),
                updateOrderRequestDTO.getOrderId(),
                updateOrderRequestDTO.getName(),
                updateOrderRequestDTO.getQuantity(),
                updateOrderRequestDTO.getTotalAmount(),
                updateOrderRequestDTO.getCategory(),
                updateOrderRequestDTO.getOrderDate(),
                updateOrderRequestDTO.getDeliveryDate(),
                updateOrderRequestDTO.getOrderStatus(),
                updateOrderRequestDTO.getWarehouse(),
                updateOrderRequestDTO.getFileName(),
                updateOrderRequestDTO.getImage()
        );
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        return warehouseOrdersService.deleteOrderById(Math.toIntExact(orderId));
    }
}
