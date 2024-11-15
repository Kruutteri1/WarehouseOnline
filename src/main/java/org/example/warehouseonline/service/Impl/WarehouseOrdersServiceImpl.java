package org.example.warehouseonline.service.Impl;

import jakarta.transaction.Transactional;
import org.example.warehouseonline.entity.WarehouseOrders;
import org.example.warehouseonline.repository.WarehouseOrdersRepository;
import org.example.warehouseonline.service.WarehouseOrdersService;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    public List<WarehouseOrders> getFilteredOrders(String warehouse, String category, String filter) {
        List<WarehouseOrders> filteredOrders;

        if (StringUtils.hasText(filter) || StringUtils.hasText(warehouse) || StringUtils.hasText(category)) {
            WarehouseOrders exampleOrder = new WarehouseOrders();
            exampleOrder.setWarehouse(StringUtils.hasText(warehouse) ? warehouse : null);
            exampleOrder.setCategory(StringUtils.hasText(category) ? category : null);
            exampleOrder.setName(StringUtils.hasText(filter) ? filter : null);

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher("warehouse", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                    .withMatcher("category", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withIgnorePaths("totalAmount");

            Example<WarehouseOrders> example = Example.of(exampleOrder, matcher);

            filteredOrders = warehouseOrdersRepository.findAll(example);
        } else {
            filteredOrders = warehouseOrdersRepository.findAll();
        }
        return filteredOrders;
    }

    @Override
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
    public WarehouseOrders addOrder(String orderId, String name, int quantity, double totalAmount, String category, String OrderDate, String deliveryDate, String warehouse, String orderStatus, String fileName, MultipartFile image) {
        byte[] imageData;
        try {
            imageData = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading an image", e);
        }

        WarehouseOrders newOrder = new WarehouseOrders();
        newOrder.setOrderId(orderId);
        newOrder.setName(name);
        newOrder.setQuantity(quantity);
        newOrder.setTotalAmount((int) totalAmount);
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
    public WarehouseOrders updateOrder(String id, String orderId, String name, int quantity, double totalAmount, String category, String orderDate, String deliveryDate, String orderStatus, String warehouse) {
        Optional<WarehouseOrders> optionalItem = warehouseOrdersRepository.findById(Long.parseLong(id));
        if (optionalItem.isEmpty()) throw new RuntimeException("Item not found with id: " + id);

        WarehouseOrders order = optionalItem.get();
        order.setOrderId(orderId);
        order.setName(name);
        order.setQuantity(quantity);
        order.setTotalAmount((int) totalAmount);
        order.setCategory(category);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedOrderDate = LocalDate.parse(orderDate, dateFormatter);
        order.setOrderDate(parsedOrderDate);
        LocalDate parsedDelivery_date = LocalDate.parse(deliveryDate, dateFormatter);
        order.setDeliveryDate(parsedDelivery_date);;

        order.setWarehouse(warehouse);
        order.setOrderStatus(orderStatus);

        return warehouseOrdersRepository.save(order);
    }

    @Override
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
