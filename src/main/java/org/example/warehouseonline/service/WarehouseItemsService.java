package org.example.warehouseonline.service;

import org.example.warehouseonline.entity.WareHouseItems;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WarehouseItemsService {
    List<WareHouseItems> getFilteredItems(String warehouse, String category, String filter);

    WareHouseItems addItem(String sku, String name, int quantity, double price, String category, String arrivalDate, String supplier, String warehouse, MultipartFile image);

    WareHouseItems updateItem(String id, String sku, String name, int quantity, double price, String category, String arrivalDate, String supplier, String warehouse);

    void deleteProductById(Integer productId);
}
