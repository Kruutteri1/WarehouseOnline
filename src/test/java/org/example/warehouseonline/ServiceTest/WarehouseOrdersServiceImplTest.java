package org.example.warehouseonline.ServiceTest;

import org.example.warehouseonline.entity.WarehouseOrders;
import org.example.warehouseonline.repository.WarehouseOrdersRepository;
import org.example.warehouseonline.service.Impl.WarehouseOrdersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseOrdersServiceImplTest {

    @Mock
    private WarehouseOrdersRepository warehouseOrdersRepository;
    @InjectMocks
    private WarehouseOrdersServiceImpl warehouseOrdersService;

    private WarehouseOrders order1;
    private WarehouseOrders order2;

    @BeforeEach
    void setUp() {
        order1 = new WarehouseOrders();
        order1.setId(1);
        order1.setOrderId("ORD12345");
        order1.setImage(new byte[]{1, 23, 44});
        order1.setFileName("image1.jpg");
        order1.setName("Apple MacBook");
        order1.setQuantity(10);
        order1.setTotalAmount(150.75);
        order1.setCategory("Electronics");
        order1.setWarehouse("Warehouse A");
        order1.setOrderDate(LocalDate.of(2024, 11, 10));
        order1.setDeliveryDate(LocalDate.of(2024, 11, 20));
        order1.setOrderStatus("Pending");

        order2 = new WarehouseOrders();
        order2.setId(2);
        order2.setOrderId("ORD67890");
        order2.setImage(new byte[]{76, 43, 87});
        order2.setFileName("image2.jpg");
        order2.setName("Product 2");
        order2.setQuantity(5);
        order2.setTotalAmount(75.50);
        order2.setCategory("Home Goods");
        order2.setWarehouse("Warehouse B");
        order2.setOrderDate(LocalDate.of(2024, 11, 11));
        order2.setDeliveryDate(LocalDate.of(2024, 11, 25));
        order2.setOrderStatus("Shipped");
    }

    @Test
    void testGetFilteredOrders_withOutFilters() {
        List<WarehouseOrders> expectedItems = List.of(order1, order2);

        when(warehouseOrdersRepository.findAll()).thenReturn(expectedItems);

        List<WarehouseOrders> filteredItems = warehouseOrdersService.getFilteredOrders("", "", "");

        assertEquals(expectedItems, filteredItems);
        verify(warehouseOrdersRepository, times(1)).findAll();
    }

    @Test
    void testGetFilteredOrders_withFilters() {
        List<WarehouseOrders> expectedItems = List.of(order1);

        WarehouseOrders exampleItem = new WarehouseOrders();
        exampleItem.setWarehouse("Warehouse A");
        exampleItem.setCategory("Electronics");
        exampleItem.setName("Apple MacBook");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("warehouse", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                .withMatcher("category", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withIgnorePaths("totalAmount");

        Example<WarehouseOrders> example = Example.of(exampleItem, matcher);

        when(warehouseOrdersRepository.findAll(example)).thenReturn(expectedItems);

        List<WarehouseOrders> filteredItems = warehouseOrdersService.getFilteredOrders("Warehouse A", "Electronics", "Apple MacBook");

        assertEquals(expectedItems, filteredItems);
        verify(warehouseOrdersRepository, times(1)).findAll(example);
    }

    @Test
    void testAddOrder() throws IOException {
        String order_id = "ORD123";
        String name = "Laptop";
        int quantity = 10;
        double total_amount = 9999;
        String category = "Electronics";
        String orderDate = "2024-11-10";
        String deliverDate = "2024-11-15";
        String warehouse = "Main Warehouse";
        String order_status = "Create";
        String fileName = "laptop.png";
        MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", new byte[] {1, 23, 4});

        when(warehouseOrdersRepository.save(any(WarehouseOrders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WarehouseOrders result = warehouseOrdersService.addOrder(order_id, name, quantity, total_amount, category,
                orderDate, deliverDate, warehouse, order_status, fileName, image);

        ArgumentCaptor<WarehouseOrders> captor = ArgumentCaptor.forClass(WarehouseOrders.class);
        verify(warehouseOrdersRepository, times(1)).save(captor.capture());
        WarehouseOrders savedItem = captor.getValue();

        assertEquals(order_id, savedItem.getOrderId());
        assertEquals(name, savedItem.getName());
        assertEquals(quantity, savedItem.getQuantity());
        assertEquals(total_amount, savedItem.getTotalAmount());
        assertEquals(category, savedItem.getCategory());
        assertEquals(LocalDate.parse(orderDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")), savedItem.getOrderDate());
        assertEquals(LocalDate.parse(deliverDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")), savedItem.getDeliveryDate());
        assertEquals(warehouse, savedItem.getWarehouse());
        assertEquals(order_status, savedItem.getOrderStatus());
        assertEquals(fileName, savedItem.getFileName());
        assertArrayEquals(image.getBytes(), savedItem.getImage());

        assertEquals(savedItem, result);
    }

    @Test
    void testAddOrder_ExceptionReadImage() throws IOException {
        String order_id = "ORD123";
        String name = "Laptop";
        int quantity = 10;
        double total_amount = 9999;
        String category = "Electronics";
        String orderDate = "2024-11-10";
        String deliverDate = "2024-11-15";
        String warehouse = "Main Warehouse";
        String order_status = "Create";
        String fileName = "laptop.png";
        MultipartFile image = mock(MultipartFile.class);

        when(image.getBytes()).thenThrow(new IOException("Error reading an image"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->{
            warehouseOrdersService.addOrder(order_id, name, quantity, total_amount, category,
                    orderDate, deliverDate, warehouse, order_status, fileName, image);
        });

        assertEquals("Error reading an image", exception.getCause().getMessage());
        verify(warehouseOrdersRepository, never()).save(any(WarehouseOrders.class));
    }

    @Test
    void testGetImage_ImageExist() {
        long orderId = order1.getId();

        when(warehouseOrdersRepository.findById(orderId)).thenReturn(Optional.of(order1));
        ResponseEntity<byte[]> response = warehouseOrdersService.getOrderImage(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals(order1.getImage(), response.getBody());
    }

    @Test
    void testGetImage_ImageNotFound() {
        long orderId = order1.getId();

        when(warehouseOrdersRepository.findById(orderId)).thenReturn(Optional.empty());
        ResponseEntity<byte[]> response = warehouseOrdersService.getOrderImage(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateOrder_OrderExist() {
        String id = "1";
        String order_id = "ORD123";
        String name = "Laptop";
        int quantity = 5;
        double total_amount = 8450;
        String category = "Electronics";
        String orderDate = "2024-11-10";
        String deliverDate = "2024-11-15";
        String warehouse = "Main Warehouse";
        String order_status = "Create";

        when(warehouseOrdersRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(order1));
        when(warehouseOrdersRepository.save(any(WarehouseOrders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WarehouseOrders updatedItem = warehouseOrdersService.updateOrder(id, order_id, name, quantity, total_amount, category,
                orderDate, deliverDate, order_status, warehouse);

        verify(warehouseOrdersRepository, times(1)).findById(Long.parseLong(id));

        ArgumentCaptor<WarehouseOrders> captor = ArgumentCaptor.forClass(WarehouseOrders.class);
        verify(warehouseOrdersRepository, times(1)).save(captor.capture());

        WarehouseOrders savedItem = captor.getValue();

        assertEquals(order_id, savedItem.getOrderId());
        assertEquals(name, savedItem.getName());
        assertEquals(quantity, savedItem.getQuantity());
        assertEquals(total_amount, savedItem.getTotalAmount());
        assertEquals(category, savedItem.getCategory());
        assertEquals(LocalDate.parse(orderDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")), savedItem.getOrderDate());
        assertEquals(LocalDate.parse(deliverDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")), savedItem.getDeliveryDate());
        assertEquals(order_status, savedItem.getOrderStatus());
        assertEquals(warehouse, savedItem.getWarehouse());

        assertEquals(savedItem, updatedItem);
    }

    @Test
    void testUpdateOrder_OrderNotFound() {
        String id = "1";
        String order_id = "ORD123";
        String name = "Laptop";
        int quantity = 5;
        double total_amount = 8450;
        String category = "Electronics";
        String orderDate = "2024-11-10";
        String deliverDate = "2024-11-15";
        String warehouse = "Main Warehouse";
        String order_status = "Create";

        when(warehouseOrdersRepository.findById(Long.parseLong(id))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> warehouseOrdersService.updateOrder(id, order_id, name, quantity, total_amount, category,
                        orderDate, deliverDate, order_status, warehouse)
        );

        assertEquals("Item not found with id: " + id, exception.getMessage());
        verify(warehouseOrdersRepository, never()).save(any(WarehouseOrders.class));
    }

    @Test
    void testDeleteOrderById_OrderExist() {
        int orderId = 1;

        when(warehouseOrdersRepository.findById(orderId)).thenReturn(Optional.of(order1));

        ResponseEntity<String> response = warehouseOrdersService.deleteOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order with ID: " + orderId + " has been deleted", response.getBody());
        verify(warehouseOrdersRepository, times(1)).findById(orderId);
    }

    @Test
    void testDeleteOrderById_InvalidNumber() {
        int orderId = -1;

        ResponseEntity<String> response = warehouseOrdersService.deleteOrderById(orderId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid order ID: must be a positive integer", response.getBody());
        verify(warehouseOrdersRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteOrderById_NotFound() {
        int orderId = 999;

        when(warehouseOrdersRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = warehouseOrdersService.deleteOrderById(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order with ID: " + orderId + " not found", response.getBody());
        verify(warehouseOrdersRepository, never()).deleteById(anyLong());
    }
}
