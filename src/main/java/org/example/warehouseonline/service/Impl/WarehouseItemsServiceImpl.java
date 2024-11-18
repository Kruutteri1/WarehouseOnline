package org.example.warehouseonline.service.Impl;

import jakarta.transaction.Transactional;
import org.example.warehouseonline.entity.WareHouseItems;
import org.example.warehouseonline.repository.WarehouseItemsRepository;
import org.example.warehouseonline.service.WarehouseItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WarehouseItemsServiceImpl implements WarehouseItemsService {

    private final WarehouseItemsRepository warehouseItemsRepository;

    @Autowired
    public WarehouseItemsServiceImpl(WarehouseItemsRepository warehouseItemsRepository) {
        this.warehouseItemsRepository = warehouseItemsRepository;
    }

    @Override
    public List<WareHouseItems> getFilteredItems(String warehouse, String category, String filter) {
        List<WareHouseItems> filteredItems;

        if (StringUtils.hasText(filter) || StringUtils.hasText(warehouse) || StringUtils.hasText(category)) {
            WareHouseItems exampleItem = new WareHouseItems();
            exampleItem.setWarehouse(StringUtils.hasText(warehouse) ? warehouse : null);
            exampleItem.setCategory(StringUtils.hasText(category) ? category : null);
            exampleItem.setName(StringUtils.hasText(filter) ? filter : null);

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher("warehouse", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                    .withMatcher("category", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withIgnorePaths("price");

            Example<WareHouseItems> example = Example.of(exampleItem, matcher);

            filteredItems = warehouseItemsRepository.findAll(example);
        } else {
            filteredItems = warehouseItemsRepository.findAll();
        }
        return filteredItems;
    }

    @Override
    public WareHouseItems addItem(String sku, String name, int quantity, double price, String category, String arrivalDate, String supplier, String warehouse, String fileName, MultipartFile image) {
        byte[] imageData;
        try {
            imageData = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading an image", e);
        }

        WareHouseItems newItem = new WareHouseItems();
        newItem.setSku(sku);
        newItem.setName(name);
        newItem.setQuantity(quantity);
        newItem.setPrice((int) price);
        newItem.setCategory(category);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedArrivalDate = LocalDate.parse(arrivalDate, dateFormatter);
        newItem.setArrivalDate(parsedArrivalDate);
        LocalDateTime currentDateTime = LocalDateTime.now();
        newItem.setLastUpdated(currentDateTime);

        newItem.setSupplier(supplier);
        newItem.setWarehouse(warehouse);
        newItem.setFileName(fileName);
        newItem.setImage(imageData);

        return warehouseItemsRepository.save(newItem);
    }

    @Override
    public ResponseEntity<byte[]> getImage(Long id) {
        Optional<WareHouseItems> itemOptional = warehouseItemsRepository.findById(id);
        if (itemOptional.isPresent()) {
            WareHouseItems item = itemOptional.get();
            byte[] imageData = item.getImage();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public WareHouseItems updateItem(String id, String sku, String name, int quantity, double price, String category, String arrivalDate, String supplier, String warehouse, String fileName, MultipartFile image) {
        byte[] imageData;
        try {
            imageData = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading an image", e);
        }

        Optional<WareHouseItems> optionalItem = warehouseItemsRepository.findById(Long.parseLong(id));
        if (optionalItem.isEmpty()) throw new RuntimeException("Item not found with id: " + id);

        WareHouseItems item = optionalItem.get();
        item.setSku(sku);
        item.setName(name);
        item.setQuantity(quantity);
        item.setPrice((int) price);
        item.setCategory(category);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedArrivalDate = LocalDate.parse(arrivalDate, dateFormatter);
        item.setArrivalDate(parsedArrivalDate);
        LocalDateTime currentDateTime = LocalDateTime.now();
        item.setLastUpdated(currentDateTime);

        item.setSupplier(supplier);
        item.setWarehouse(warehouse);
        item.setFileName(fileName);
        item.setImage(imageData);
        return warehouseItemsRepository.save(item);
    }

    @Override
    public ResponseEntity<String> deleteProductById(Integer productId) {
        if (productId < 0) return new ResponseEntity<>("Invalid product ID: must be a positive integer", HttpStatus.BAD_REQUEST);
        Optional<WareHouseItems> product = warehouseItemsRepository.findById((long) productId);

        if (product.isPresent()) {
            warehouseItemsRepository.deleteById((long) productId);
            return new ResponseEntity<>("Product with ID: " + productId + " has been deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product with ID: " + productId + " not found", HttpStatus.NOT_FOUND);
        }
    }
}
