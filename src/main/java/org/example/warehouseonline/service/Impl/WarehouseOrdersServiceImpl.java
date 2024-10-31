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
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

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
    public WarehouseOrders addOrder(String order_id, String name, int quantity, double total_amount, String category, String OrderDate, String delivery_date, String warehouse, String order_status, String fileName,MultipartFile image) {
        byte[] imageData;
        try {
            imageData = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading an image", e);
        }

        // Создаем новый объект WareHouseOrder с полученными данными
        WarehouseOrders newOrder = new WarehouseOrders();
        newOrder.setOrder_id(order_id);
        newOrder.setName(name);
        newOrder.setQuantity(quantity);
        newOrder.setTotal_amount((int) total_amount);
        newOrder.setCategory(category);

        // Форматтер для даты
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedOrderDate = LocalDate.parse(OrderDate, dateFormatter);
        newOrder.setOrderDate(parsedOrderDate);

        LocalDate parsedDeliveryDate = LocalDate.parse(delivery_date, dateFormatter);
        newOrder.setDelivery_date(parsedDeliveryDate);

        newOrder.setWarehouse(warehouse);
        newOrder.setOrder_status(order_status);
        newOrder.setFileName(fileName);
        newOrder.setImage(imageData);

        return warehouseOrdersRepository.save(newOrder);
    }

    @Override
    public WarehouseOrders updateOrder(String id, String order_id, String name, int quantity, double total_amount, String category, String orderDate, String delivery_date, String order_status, String warehouse) {
        // Найти товар в базе данных по его идентификатору
        Optional<WarehouseOrders> optionalItem = warehouseOrdersRepository.findById(Long.parseLong(id));
        if (optionalItem.isEmpty()) {
            throw new RuntimeException("Item not found with id: " + id);
        }

        WarehouseOrders order = optionalItem.get();

        // Обновить поля товара на основе полученных данных
        order.setOrder_id(order_id);
        order.setName(name);
        order.setQuantity(quantity);
        order.setTotal_amount((int) total_amount);
        order.setCategory(category);

        // Форматтер для даты прибытия
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedOrderDate = LocalDate.parse(orderDate, dateFormatter);
        order.setOrderDate(parsedOrderDate);

        LocalDate parsedDelivery_date = LocalDate.parse(delivery_date, dateFormatter);
        order.setDelivery_date(parsedDelivery_date);;

        order.setWarehouse(warehouse);
        order.setOrder_status(order_status);

        return warehouseOrdersRepository.save(order);
    }

    @Override
    public ResponseEntity<String> deleteOrderById(Integer orderId) {
        Optional<WarehouseOrders> order = warehouseOrdersRepository.findById(orderId);

        if (order.isPresent()) {
            warehouseOrdersRepository.deleteById((long) orderId);
            return new ResponseEntity<>("Order with ID: " + orderId + " has been deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Order with ID: " + orderId + " not found", HttpStatus.NOT_FOUND);
        }
    }
}
