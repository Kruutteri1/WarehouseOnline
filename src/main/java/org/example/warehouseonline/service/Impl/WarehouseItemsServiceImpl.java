package org.example.warehouseonline.service.Impl;

import jakarta.transaction.Transactional;
import org.example.warehouseonline.entity.WareHouseItems;
import org.example.warehouseonline.repository.WarehouseItemsRepository;
import org.example.warehouseonline.service.WarehouseItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
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
import java.time.format.DateTimeParseException;
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
    @Cacheable(value = "filteredItems", key = "#page + '-' + #size + '-' + #warehouse + '-' + #category + '-' + #filter")
    public ResponseEntity<Page<WareHouseItems>> getFilteredItems(int page, int size, String warehouse, String category, String filter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<WareHouseItems> filteredItems;

        if (StringUtils.hasText(warehouse) || StringUtils.hasText(category) || StringUtils.hasText(filter)) {
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

            filteredItems = warehouseItemsRepository.findAll(example, pageable);
        } else {
            filteredItems = warehouseItemsRepository.findAll(pageable);
        }
        return ResponseEntity.ok(filteredItems);
    }

    @Override
    @CachePut(value = "filteredItems", key = "#result.id")
    public WareHouseItems addItem(String sku, String name, Integer quantity, Double price, String category, String arrivalDate, String supplier, String warehouse, String fileName, MultipartFile image) {
        byte[] imageData;
        try {
            imageData = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading an image", e);
        }

        if (warehouseItemsRepository.findBySku(sku).isPresent()) {
            throw new IllegalArgumentException("Item with SKU " + sku + " already exists. Please use a different SKU.");
        }

        WareHouseItems newItem = new WareHouseItems();
        newItem.setSku(sku);
        newItem.setName(name);
        newItem.setQuantity(quantity);
        newItem.setPrice((price));
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
    @Cacheable(value = "itemImage", key = "#id")
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
    @CacheEvict(value = {"filteredItems", "itemImage"}, key = "#id")
    public WareHouseItems updateItem(String id, String sku, String name, Integer quantity, Double price, String category, String arrivalDate, String supplier, String warehouse, String fileName, MultipartFile image) {
        Optional<WareHouseItems> optionalItem = warehouseItemsRepository.findById(Long.parseLong(id));
        if (optionalItem.isEmpty()) throw new RuntimeException("Item not found with id: " + id);

        WareHouseItems item = optionalItem.get();

        if (StringUtils.hasText(sku)) {
            item.setSku(sku);
        }

        if (StringUtils.hasText(name)) {
            item.setName(name);
        }

        if (quantity != null) {
            item.setQuantity(quantity);
        }

        if (price != null) {
            item.setPrice(price);
        }

        if (StringUtils.hasText(category)) {
            item.setCategory(category);
        }

        if (StringUtils.hasText(arrivalDate)) {
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedArrivalDate = LocalDate.parse(arrivalDate, dateFormatter);
                item.setArrivalDate(parsedArrivalDate);
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid arrival date format. Expected format is yyyy-MM-dd: " + arrivalDate, e);
            }
        }

        if (StringUtils.hasText(supplier)) {
            item.setSupplier(supplier);
        }

        if (StringUtils.hasText(warehouse)) {
            item.setWarehouse(warehouse);
        }

        if (StringUtils.hasText(fileName)) {
            item.setFileName(fileName);
        }

        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageData = image.getBytes();
                item.setImage(imageData);
            } catch (IOException e) {
                throw new RuntimeException("Error reading the image file", e);
            }
        }
        item.setLastUpdated(LocalDateTime.now());

        return warehouseItemsRepository.save(item);
    }


    @Override
    @CacheEvict(value = {"filteredItems", "itemImage"}, key = "#productId")
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
