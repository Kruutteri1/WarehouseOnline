package org.example.warehouseonline.service;

import org.example.warehouseonline.entity.WarehouseOrders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WarehouseOrdersService {
    List<WarehouseOrders> getFilteredOrders(String warehouse, String category, String filter);

    WarehouseOrders addItem(String order_id, String name, int quantity, double total_amount, String category, String OrderDate, String delivery_date, String warehouse, String order_status, MultipartFile image);

    WarehouseOrders updateOrder(String id, String order_id, String name, int quantity, double total_amount, String category, String orderDate, String delivery_date, String order_status, String warehouse);

    void deleteOrderById(Integer productId);
}
