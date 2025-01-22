package org.example.warehouseonline.service;

import org.example.warehouseonline.entity.WarehouseOrders;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public interface WarehouseOrdersService {
    ResponseEntity<Page<WarehouseOrders>> getFilteredOrders(int page, int size, String warehouse, String category, String status, String filter);

    ResponseEntity<byte[]> getOrderImage(Long id);

    WarehouseOrders addOrder(String orderId, String name, Integer quantity, Double totalAmount, String category, String OrderDate, String deliveryDate, String warehouse, String orderStatus, String fileName, MultipartFile image);

    WarehouseOrders updateOrder(String id, String orderId, String name, Integer quantity, Double totalAmount, String category, String orderDate, String deliveryDate, String orderStatus, String warehouse, String fileName, MultipartFile image);

    ResponseEntity<String> deleteOrderById(Integer productId);
}
