package org.example.warehouseonline.service.Impl;

import jakarta.transaction.Transactional;
import org.example.warehouseonline.entity.WarehouseOrders;
import org.example.warehouseonline.repository.WarehouseOrdersRepository;
import org.example.warehouseonline.service.WarehouseOrdersService;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@Transactional
public class WarehouseOrdersServiceImpl implements WarehouseOrdersService {
    private final WarehouseOrdersRepository warehouseOrdersRepository;

    @Autowired
    public WarehouseOrdersServiceImpl(WarehouseOrdersRepository warehouseOrdersRepository) {
        this.warehouseOrdersRepository = warehouseOrdersRepository;
    }

    @Override
    @Cacheable(value = "filteredOrders", key = "#page + '-' + #size + '-' + #warehouse + '-' + #category + '-' + #status + '-' + #filter")
    public ResponseEntity<Page<WarehouseOrders>> getFilteredOrders(int page, int size, String warehouse, String category, String status, String filter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<WarehouseOrders> filteredOrders;

        if (StringUtils.hasText(filter) || StringUtils.hasText(warehouse) || StringUtils.hasText(category) || StringUtils.hasText(status)) {
            WarehouseOrders exampleOrder = new WarehouseOrders();
            exampleOrder.setWarehouse(StringUtils.hasText(warehouse) ? warehouse : null);
            exampleOrder.setCategory(StringUtils.hasText(category) ? category : null);
            exampleOrder.setOrderStatus(StringUtils.hasText(status) ? status : null);
            exampleOrder.setName(StringUtils.hasText(filter) ? filter : null);

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher("warehouse", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                    .withMatcher("category", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                    .withMatcher("order_status", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withIgnorePaths("totalAmount");

            Example<WarehouseOrders> example = Example.of(exampleOrder, matcher);

            filteredOrders = warehouseOrdersRepository.findAll(example, pageable);
        } else {
            filteredOrders = warehouseOrdersRepository.findAll(pageable);
        }
        return ResponseEntity.ok(filteredOrders);
    }

    @Override
    @Cacheable(value = "orderImages", key = "#id")
    public ResponseEntity<byte[]> getOrderImage(Long id) {
        Optional<WarehouseOrders> itemOptional = warehouseOrdersRepository.findById(id);
        if (itemOptional.isPresent()) {
            WarehouseOrders order = itemOptional.get();
            byte[] imageData = order.getImage();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @CachePut(value = "filteredOrders", key = "#result.id")
    public WarehouseOrders addOrder(String orderId, String name, Integer quantity, Double totalAmount, String category, String OrderDate, String deliveryDate, String warehouse, String orderStatus, String fileName, MultipartFile image) {
        byte[] imageData;
        try {
            imageData = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading an image", e);
        }

        if (warehouseOrdersRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalArgumentException("Order with OrderId " + orderId + " already exists. Please use a different OrderId.");
        }

        WarehouseOrders newOrder = new WarehouseOrders();
        newOrder.setOrderId(orderId);
        newOrder.setName(name);
        newOrder.setQuantity(quantity);
        newOrder.setTotalAmount(totalAmount);
        newOrder.setCategory(category);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedOrderDate = LocalDate.parse(OrderDate, dateFormatter);
        newOrder.setOrderDate(parsedOrderDate);

        LocalDate parsedDeliveryDate = LocalDate.parse(deliveryDate, dateFormatter);
        newOrder.setDeliveryDate(parsedDeliveryDate);

        newOrder.setWarehouse(warehouse);
        newOrder.setOrderStatus(orderStatus);
        newOrder.setFileName(fileName);
        newOrder.setImage(imageData);

        return warehouseOrdersRepository.save(newOrder);
    }

    @Override
    @CacheEvict(value = {"filteredOrders", "orderImages"}, key = "#id")
    public WarehouseOrders updateOrder(String id, String orderId, String name, Integer quantity, Double totalAmount, String category, String orderDate, String deliveryDate, String orderStatus, String warehouse, String fileName, MultipartFile image) {
        Optional<WarehouseOrders> optionalItem = warehouseOrdersRepository.findById(Long.parseLong(id));
        if (optionalItem.isEmpty()) throw new RuntimeException("Item not found with id: " + id);

        WarehouseOrders order = optionalItem.get();

        if (StringUtils.hasText(orderId)) {
            order.setOrderId(orderId);
        }

        if (StringUtils.hasText(name)) {
            order.setName(name);
        }

        if (quantity != null) {
            order.setQuantity(quantity);
        }

        if (totalAmount != null) {
            order.setTotalAmount(totalAmount);
        }

        if (StringUtils.hasText(category)) {
            order.setCategory(category);
        }

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (StringUtils.hasText(orderDate)) {
                LocalDate parsedOrderDate = LocalDate.parse(orderDate, dateFormatter);
                order.setOrderDate(parsedOrderDate);
            }

            if (StringUtils.hasText(deliveryDate)) {
                LocalDate parsedDeliveryDate = LocalDate.parse(deliveryDate, dateFormatter);
                order.setDeliveryDate(parsedDeliveryDate);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format, please use yyyy-MM-dd");
        }

        if (StringUtils.hasText(warehouse)) {
            order.setWarehouse(warehouse);
        }

        if (StringUtils.hasText(orderStatus)) {
            order.setOrderStatus(orderStatus);
        }

        if (StringUtils.hasText(fileName)) {
            order.setFileName(fileName);
        }

        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageData = image.getBytes();
                order.setImage(imageData);
            } catch (IOException e) {
                throw new RuntimeException("Error reading an image", e);
            }
        }

        return warehouseOrdersRepository.save(order);
    }

    @Override
    @CacheEvict(value = {"filteredOrders", "orderImages"}, key = "#orderId")
    public ResponseEntity<String> deleteOrderById(Integer orderId) {
        if (orderId < 0) return new ResponseEntity<>("Invalid order ID: must be a positive integer", HttpStatus.BAD_REQUEST);
        Optional<WarehouseOrders> order = warehouseOrdersRepository.findById((long) orderId);

        if (order.isPresent()) {
            warehouseOrdersRepository.deleteById((long) orderId);
            return new ResponseEntity<>("Order with ID: " + orderId + " has been deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Order with ID: " + orderId + " not found", HttpStatus.NOT_FOUND);
        }
    }
}
