package org.example.warehouseonline.ControllerTest;

import org.example.warehouseonline.controller.WarehouseItemsController;
import org.example.warehouseonline.entity.WareHouseItems;
import org.example.warehouseonline.service.Impl.WarehouseItemsServiceImpl;
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
class WarehouseItemsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private WarehouseItemsController warehouseItemsController;

    @Mock
    private WarehouseItemsServiceImpl warehouseItemsService;

    private WareHouseItems item1;
    private WareHouseItems item2;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(warehouseItemsController).build();

        item1 = new WareHouseItems();
        item1.setId(1);
        item1.setName("Item 1");
        item1.setCategory("Electronics");
        item1.setWarehouse("Warehouse A");

        item2 = new WareHouseItems();
        item2.setId(2);
        item2.setName("Item 2");
        item2.setCategory("Home Goods");
        item2.setWarehouse("Warehouse B");

        jwtToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYWluMjFAZ21haWwuY29tIiwiaWF0Ij" +
                "oxNzMxNDI0NzE3LCJleHAiOjE3MzE1MTExMTd9.0ga9q0rCmIUMR9ZbKKKc0Ts_3fQL_cXpcl-1izGWClw"; // replace with a new actual token
    }

    @Test
    public void getAllItems_WithFilterParams_ShouldReturnFilteredItems() throws Exception {
        String warehouse = "Warehouse A";
        String category = "Electronics";
        String filter = "Item 1";
        List<WareHouseItems> items = Arrays.asList(item1);

        when(warehouseItemsService.getFilteredItems(warehouse, category, filter)).thenReturn(items);

        mockMvc.perform(get("/api/warehouse/items")
                        .header("Authorization", jwtToken)
                        .param("warehouse", warehouse)
                        .param("category", category)
                        .param("filter", filter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"));
    }

    @Test
    public void getAllItems_WithoutParams_ShouldReturnAllItems() throws Exception {
        List<WareHouseItems> items = Arrays.asList(item1, item2);

        when(warehouseItemsService.getFilteredItems(null, null, null)).thenReturn(items);

        mockMvc.perform(get("/api/warehouse/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].name").value("Item 2"));
    }

    @Test
    public void getImage_ValidId_ShouldReturnImage() throws Exception {
        long productId = 1;
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};

        when(warehouseItemsService.getImage(productId)).thenReturn(new ResponseEntity<>(imageData, HttpStatus.OK));

        mockMvc.perform(get("/api/warehouse/items/image/{id}", productId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageData));
    }

    @Test
    public void addItem_ValidParameters_ShouldReturnAddedItem() throws Exception {
        String sku = "12345";
        String name = "Item 1";
        int quantity = 10;
        double price = 20.5;
        String category = "Electronics";
        String arrivalDate = "2024-11-12";
        String supplier = "Supplier A";
        String warehouse = "Warehouse A";
        String fileName = "item1.jpg";
        MockMultipartFile image = new MockMultipartFile("image", "item1.jpg", "image/jpeg", "image data".getBytes());

        WareHouseItems item = new WareHouseItems();
        item.setSku(sku);
        item.setName(name);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setCategory(category);
        item.setArrivalDate(LocalDate.parse(arrivalDate));
        item.setSupplier(supplier);
        item.setWarehouse(warehouse);
        item.setFileName(fileName);

        when(warehouseItemsService.addItem(eq(sku), eq(name), eq(quantity), eq(price), eq(category), eq(arrivalDate), eq(supplier), eq(warehouse), eq(fileName), any(MultipartFile.class)))
                .thenReturn(item);

        mockMvc.perform(multipart("/api/warehouse/items/add")
                        .file(image)
                        .param("sku", sku)
                        .param("name", name)
                        .param("quantity", String.valueOf(quantity))
                        .param("price", String.valueOf(price))
                        .param("category", category)
                        .param("arrivalDate", arrivalDate)
                        .param("supplier", supplier)
                        .param("warehouse", warehouse)
                        .param("fileName", fileName)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(sku))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.quantity").value(quantity))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.arrivalDate[0]").value(2024))
                .andExpect(jsonPath("$.arrivalDate[1]").value(11))
                .andExpect(jsonPath("$.arrivalDate[2]").value(12))
                .andExpect(jsonPath("$.supplier").value(supplier))
                .andExpect(jsonPath("$.warehouse").value(warehouse))
                .andExpect(jsonPath("$.fileName").value(fileName));
        verify(warehouseItemsService, times(1)).addItem(
                eq(sku), eq(name), eq(quantity), eq(price), eq(category),
                eq(arrivalDate), eq(supplier), eq(warehouse), eq(fileName), any(MultipartFile.class));
    }

    @Test
    public void updateItem_ValidParameters_ShouldReturnUpdatedItem() throws Exception {
        String id = "1";
        String sku = "12345";
        String name = "Updated Item";
        int quantity = 20;
        double price = 30.5;
        String category = "Electronics";
        String arrivalDate = "2024-11-12";
        String supplier = "Updated Supplier";
        String warehouse = "Updated Warehouse";

        WareHouseItems updatedItem = new WareHouseItems();
        updatedItem.setId(Integer.valueOf(id));
        updatedItem.setSku(sku);
        updatedItem.setName(name);
        updatedItem.setQuantity(quantity);
        updatedItem.setPrice(price);
        updatedItem.setCategory(category);
        updatedItem.setArrivalDate(LocalDate.parse(arrivalDate));
        updatedItem.setSupplier(supplier);
        updatedItem.setWarehouse(warehouse);

        when(warehouseItemsService.updateItem(eq(id), eq(sku), eq(name), eq(quantity), eq(price), eq(category), eq(arrivalDate), eq(supplier), eq(warehouse)))
                .thenReturn(updatedItem);

        mockMvc.perform(post("/api/warehouse/items/update")
                        .param("id", id)
                        .param("sku", sku)
                        .param("name", name)
                        .param("quantity", String.valueOf(quantity))
                        .param("price", String.valueOf(price))
                        .param("category", category)
                        .param("arrivalDate", arrivalDate)
                        .param("supplier", supplier)
                        .param("warehouse", warehouse)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.sku").value(sku))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.quantity").value(quantity))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.arrivalDate[0]").value(2024))
                .andExpect(jsonPath("$.arrivalDate[1]").value(11))
                .andExpect(jsonPath("$.arrivalDate[2]").value(12))
                .andExpect(jsonPath("$.supplier").value(supplier))
                .andExpect(jsonPath("$.warehouse").value(warehouse));
        verify(warehouseItemsService, times(1)).updateItem(eq(id), eq(sku), eq(name), eq(quantity), eq(price),
                eq(category), eq(arrivalDate), eq(supplier), eq(warehouse));
    }

    @Test
    public void getImage_InvalidId_ShouldReturnNotFound() throws Exception {
        long productId = 999;
        when(warehouseItemsService.getImage(productId)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/warehouse/items/image/{id}", productId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteProduct_ValidId_ShouldReturnOk() throws Exception {
        int productId = 1;
        when(warehouseItemsService.deleteProductById(productId))
                .thenReturn(new ResponseEntity<>("Product with ID: " + productId + " has been deleted", HttpStatus.OK));

        mockMvc.perform(delete("/api/warehouse/items/delete/{productId}", productId)
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Product with ID: " + productId + " has been deleted"));
        verify(warehouseItemsService, times(1)).deleteProductById(productId);
    }

    @Test
    public void deleteProduct_NotValidId_ShouldReturnBadRequest() throws Exception {
        int productId = -1;
        when(warehouseItemsService.deleteProductById(productId))
                .thenReturn(new ResponseEntity<>("Invalid product ID: must be a positive integer", HttpStatus.BAD_REQUEST));

        mockMvc.perform(delete("/api/warehouse/items/delete/{productId}", productId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid product ID: must be a positive integer"));
    }

    @Test
    public void deleteProduct_NotFoundId_ShouldReturnNotFound() throws Exception {
        int productId = 999;
        when(warehouseItemsService.deleteProductById(productId))
                .thenReturn(new ResponseEntity<>("Product with ID: " + productId + " not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(delete("/api/warehouse/items/delete/{productId}", productId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product with ID: " + productId + " not found"));
    }
}
