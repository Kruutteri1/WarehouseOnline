package org.example.warehouseonline.service;

import org.example.warehouseonline.entity.WarehouseOrders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WarehouseOrdersService {
    List<WarehouseOrders> getFilteredOrders(String warehouse, String category, String filter);

    ResponseEntity<byte[]> getOrderImage(Long id);

    WarehouseOrders addOrder(String order_id, String name, int quantity, double total_amount, String category, String OrderDate, String delivery_date, String warehouse, String order_status, String fileName, MultipartFile image);

    WarehouseOrders updateOrder(String id, String order_id, String name, int quantity, double total_amount, String category, String orderDate, String delivery_date, String order_status, String warehouse);

    ResponseEntity<String> deleteOrderById(Integer productId);
}
