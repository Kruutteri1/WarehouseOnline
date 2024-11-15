package org.example.warehouseonline.ControllerTest;

import org.example.warehouseonline.controller.WarehouseOrdersController;
import org.example.warehouseonline.entity.WarehouseOrders;
import org.example.warehouseonline.service.Impl.WarehouseOrdersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WarehouseOrdersControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WarehouseOrdersServiceImpl warehouseOrdersService;

    @InjectMocks
    private WarehouseOrdersController warehouseOrdersController;

    private WarehouseOrders order1;
    private WarehouseOrders order2;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(warehouseOrdersController).build();

        order1 = new WarehouseOrders();
        order1.setId(1);
        order1.setOrderId("O-12345");
        order1.setName("Sample Order");
        order1.setQuantity(8);
        order1.setTotalAmount(1240.0);
        order1.setCategory("Book");
        order1.setOrderDate(LocalDate.parse("2024-11-13"));
        order1.setDeliveryDate(LocalDate.parse("2024-11-16"));
        order1.setWarehouse("Main Warehouse");

        order2 = new WarehouseOrders();
        order2.setId(2);
        order2.setOrderId("O-12313431");
        order2.setName("Second Order");
        order2.setQuantity(14);
        order2.setTotalAmount(13443.1);
        order2.setCategory("Electronics");
        order2.setOrderDate(LocalDate.parse("2024-11-13"));
        order2.setDeliveryDate(LocalDate.parse("2024-11-20"));
        order2.setWarehouse("Warehouse C");

        jwtToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYWluMjFAZ21haWwuY29tIiwiaWF0Ij" +
                "oxNzMxNDI0NzE3LCJleHAiOjE3MzE1MTExMTd9.0ga9q0rCmIUMR9ZbKKKc0Ts_3fQL_cXpcl-1izGWClw"; // replace with a new actual token
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() throws Exception {
        List<WarehouseOrders> orders = Arrays.asList(order1, order2);

        when(warehouseOrdersService.getFilteredOrders(null, null, null)).thenReturn(orders);

        mockMvc.perform(get("/api/warehouse/orders")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order1.getId()))
                .andExpect(jsonPath("$[0].orderId").value(order1.getOrderId()))
                .andExpect(jsonPath("$[0].name").value(order1.getName()))
                .andExpect(jsonPath("$[0].quantity").value(order1.getQuantity()))
                .andExpect(jsonPath("$[0].totalAmount").value(order1.getTotalAmount()))
                .andExpect(jsonPath("$[0].category").value(order1.getCategory()))
                .andExpect(jsonPath("$[0].orderDate[0]").value(2024))
                .andExpect(jsonPath("$[0].orderDate[1]").value(11))
                .andExpect(jsonPath("$[0].orderDate[2]").value(13))
                .andExpect(jsonPath("$[0].deliveryDate[0]").value(2024))
                .andExpect(jsonPath("$[0].deliveryDate[1]").value(11))
                .andExpect(jsonPath("$[0].deliveryDate[2]").value(16))
                .andExpect(jsonPath("$[0].warehouse").value(order1.getWarehouse()))
                // Verify order2 details
                .andExpect(jsonPath("$[1].id").value(order2.getId()))
                .andExpect(jsonPath("$[1].orderId").value(order2.getOrderId()))
                .andExpect(jsonPath("$[1].name").value(order2.getName()))
                .andExpect(jsonPath("$[1].quantity").value(order2.getQuantity()))
                .andExpect(jsonPath("$[1].totalAmount").value(order2.getTotalAmount()))
                .andExpect(jsonPath("$[1].category").value(order2.getCategory()))
                .andExpect(jsonPath("$[1].orderDate[0]").value(2024))
                .andExpect(jsonPath("$[1].orderDate[1]").value(11))
                .andExpect(jsonPath("$[1].orderDate[2]").value(13))
                .andExpect(jsonPath("$[1].deliveryDate[0]").value(2024))
                .andExpect(jsonPath("$[1].deliveryDate[1]").value(11))
                .andExpect(jsonPath("$[1].deliveryDate[2]").value(20))
                .andExpect(jsonPath("$[1].warehouse").value(order2.getWarehouse()));
        verify(warehouseOrdersService, times(1)).getFilteredOrders(null, null, null);
    }

    @Test
    public void getAllOrders_WithFilterParams_ShouldReturnFilteredOrders() throws Exception {
        String warehouse = "Warehouse A";
        String category = "Electronics";
        String filter = "Order 1";
        List<WarehouseOrders> orders = Arrays.asList(order1);

        when(warehouseOrdersService.getFilteredOrders(warehouse, category, filter)).thenReturn(orders);

        mockMvc.perform(get("/api/warehouse/orders")
                        .header("Authorization", jwtToken)
                        .param("warehouse", warehouse)
                        .param("category", category)
                        .param("filter", filter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order1.getId()))
                .andExpect(jsonPath("$[0].orderId").value(order1.getOrderId()))
                .andExpect(jsonPath("$[0].name").value(order1.getName()))
                .andExpect(jsonPath("$[0].quantity").value(order1.getQuantity()))
                .andExpect(jsonPath("$[0].totalAmount").value(order1.getTotalAmount()))
                .andExpect(jsonPath("$[0].category").value(order1.getCategory()))
                .andExpect(jsonPath("$[0].orderDate[0]").value(2024))
                .andExpect(jsonPath("$[0].orderDate[1]").value(11))
                .andExpect(jsonPath("$[0].orderDate[2]").value(13))
                .andExpect(jsonPath("$[0].deliveryDate[0]").value(2024))
                .andExpect(jsonPath("$[0].deliveryDate[1]").value(11))
                .andExpect(jsonPath("$[0].deliveryDate[2]").value(16))
                .andExpect(jsonPath("$[0].warehouse").value(order1.getWarehouse()));
        verify(warehouseOrdersService, times(1)).getFilteredOrders("Warehouse A", "Electronics", "Order 1");
    }

    @Test
    public void getOrderImage_ValidId_ShouldReturnImage() throws Exception {
        long orderId = 1;
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};

        when(warehouseOrdersService.getOrderImage(orderId)).thenReturn(new ResponseEntity<>(imageData, HttpStatus.OK));

        mockMvc.perform(get("/api/warehouse/orders/oderImage/{id}", orderId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageData));
        verify(warehouseOrdersService, times(1)).getOrderImage(orderId);
    }

    @Test
    public void getOrderImage_InvalidId_ShouldReturnNotFound() throws Exception {
        long orderId = 999;
        when(warehouseOrdersService.getOrderImage(orderId)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/warehouse/orders/oderImage/{id}", orderId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addOrder_ValidParameters_ShouldReturnAddedOrder() throws Exception {
        String orderId = "12345";
        String name = "Order 1";
        int quantity = 10;
        double totalAmount = 100.0;
        String category = "Electronics";
        String orderDate = "2024-11-12";
        String deliveryDate = "2024-11-20";
        String warehouse = "Warehouse A";
        String fileName = "order1.jpg";
        String orderStatus = "Pending";
        MockMultipartFile image = new MockMultipartFile("image", "order1.jpg", "image/jpeg", "image data".getBytes());

        WarehouseOrders order = new WarehouseOrders();
        order.setOrderId(orderId);
        order.setName(name);
        order.setQuantity(quantity);
        order.setTotalAmount(totalAmount);
        order.setCategory(category);
        order.setOrderDate(LocalDate.parse(orderDate));
        order.setDeliveryDate(LocalDate.parse(deliveryDate));
        order.setWarehouse(warehouse);
        order.setFileName(fileName);
        order.setOrderStatus(orderStatus);

        when(warehouseOrdersService.addOrder(eq(orderId), eq(name), eq(quantity), eq(totalAmount), eq(category), eq(orderDate), eq(deliveryDate), eq(warehouse), eq(orderStatus), eq(fileName), any(MultipartFile.class)))
                .thenReturn(order);

        mockMvc.perform(multipart("/api/warehouse/orders/add")
                        .file(image)
                        .param("orderId", orderId)
                        .param("name", name)
                        .param("quantity", String.valueOf(quantity))
                        .param("totalAmount", String.valueOf(totalAmount))
                        .param("category", category)
                        .param("orderDate", orderDate)
                        .param("deliveryDate", deliveryDate)
                        .param("warehouse", warehouse)
                        .param("fileName", fileName)
                        .param("orderStatus", orderStatus)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.quantity").value(quantity))
                .andExpect(jsonPath("$.totalAmount").value(totalAmount))
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.orderDate[0]").value(2024))
                .andExpect(jsonPath("$.orderDate[1]").value(11))
                .andExpect(jsonPath("$.orderDate[2]").value(12))
                .andExpect(jsonPath("$.warehouse").value(warehouse))
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.orderStatus").value(orderStatus));
        verify(warehouseOrdersService, times(1)).addOrder(eq(orderId), eq(name), eq(quantity), eq(totalAmount), eq(category),
                eq(orderDate), eq(deliveryDate), eq(warehouse), eq(orderStatus), eq(fileName), any(MultipartFile.class));
    }


    @Test
    public void deleteOrder_ValidId_ShouldReturnOk() throws Exception {
        int orderId = 1;
        when(warehouseOrdersService.deleteOrderById(orderId))
                .thenReturn(new ResponseEntity<>("Order with ID: " + orderId + " has been deleted", HttpStatus.OK));

        mockMvc.perform(delete("/api/warehouse/orders/delete/{orderId}", orderId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Order with ID: " + orderId + " has been deleted"));
        verify(warehouseOrdersService, times(1)).deleteOrderById(orderId);
    }

    @Test
    public void deleteOrder_NotFoundId_ShouldReturnNotFound() throws Exception {
        int orderId = 999;
        when(warehouseOrdersService.deleteOrderById(orderId))
                .thenReturn(new ResponseEntity<>("Order with ID: " + orderId + " not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(delete("/api/warehouse/orders/delete/{orderId}", orderId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order with ID: " + orderId + " not found"));
    }

    @Test
    public void deleteProduct_NotValidId_ShouldReturnBadRequest() throws Exception {
        int orderId = -1;
        when(warehouseOrdersService.deleteOrderById(orderId))
                .thenReturn(new ResponseEntity<>("Invalid order ID: must be a positive integer", HttpStatus.BAD_REQUEST));

        mockMvc.perform(delete("/api/warehouse/orders/delete/{orderId}", orderId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid order ID: must be a positive integer"));
    }
}
