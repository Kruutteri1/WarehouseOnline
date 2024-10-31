package org.example.warehouseonline.service;

import org.example.warehouseonline.entity.WareHouseItems;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WarehouseItemsService {
    List<WareHouseItems> getFilteredItems(String warehouse, String category, String filter);

    ResponseEntity<byte[]> getImage(Long id);

    WareHouseItems addItem(String sku, String name, int quantity, double price, String category, String arrivalDate, String supplier, String warehouse, String fileName, MultipartFile image);

    WareHouseItems updateItem(String id, String sku, String name, int quantity, double price, String category, String arrivalDate, String supplier, String warehouse);

    ResponseEntity<String> deleteProductById(Integer productId);
}
