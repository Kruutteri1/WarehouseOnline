package org.example.warehouseonline.ServiceTest;

import org.example.warehouseonline.entity.WareHouseItems;
import org.example.warehouseonline.repository.WarehouseItemsRepository;
import org.example.warehouseonline.service.Impl.WarehouseItemsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.ExampleMatcher;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseItemsServiceImplTest {

    @Mock
    private WarehouseItemsRepository warehouseItemsRepository;

    @InjectMocks
    private WarehouseItemsServiceImpl warehouseItemsService;

    private WareHouseItems item1;
    private WareHouseItems item2;

    @BeforeEach
    void setUp() {
        item1 = new WareHouseItems();
        item1.setId(1);
        item1.setName("Apple MacBook");
        item1.setWarehouse("Main Warehouse");
        item1.setCategory("Electronics");
        item1.setQuantity(10);
        item1.setPrice(5999.40);
        item1.setArrivalDate(LocalDate.of(2024, 11, 10));
        item1.setLastUpdated(LocalDateTime.now());
        item1.setImage("image data 1".getBytes(StandardCharsets.UTF_8));

        item2 = new WareHouseItems();
        item2.setId(2);
        item2.setName("Saga");
        item2.setWarehouse("Warehouse C");
        item2.setCategory("Book");
        item2.setQuantity(2);
        item2.setPrice(2000);
        item2.setArrivalDate(LocalDate.of(2024, 11, 11));
        item2.setLastUpdated(LocalDateTime.now());
        item2.setImage("image data 2".getBytes(StandardCharsets.UTF_8));
    }
    @Test
    void testGetFilteredItems_withOutFilters() {
        List<WareHouseItems> expectedItems = List.of(item1, item2);

        when(warehouseItemsRepository.findAll()).thenReturn(expectedItems);

        List<WareHouseItems> filteredItems = warehouseItemsService.getFilteredItems("", "", "");

        assertEquals(expectedItems, filteredItems);
        verify(warehouseItemsRepository, times(1)).findAll();
    }

    @Test
    void testGetFilteredItems_withFilters() {
        List<WareHouseItems> expectedItems = List.of(item1);

        WareHouseItems exampleItem = new WareHouseItems();
        exampleItem.setWarehouse("Main Warehouse");
        exampleItem.setCategory("Electronics");
        exampleItem.setName("Apple MacBook");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("warehouse", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                .withMatcher("category", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withIgnorePaths("price");

        Example<WareHouseItems> example = Example.of(exampleItem, matcher);

        when(warehouseItemsRepository.findAll(example)).thenReturn(expectedItems);

        List<WareHouseItems> filteredItems = warehouseItemsService.getFilteredItems("Main Warehouse", "Electronics", "Apple MacBook");

        assertEquals(expectedItems, filteredItems);
        verify(warehouseItemsRepository, times(1)).findAll(example);
    }

    @Test
    void testAddItem() throws IOException {
        String sku = "ORD123";
        String name = "Laptop";
        int quantity = 10;
        double price = 9999;
        String category = "Electronics";
        String arrivalDate = "2024-11-10";
        String supplier = "Tech Supplier";
        String warehouse = "Main Warehouse";
        String fileName = "laptop.png";
        MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", new byte[] {1, 23, 4});

        when(warehouseItemsRepository.save(any(WareHouseItems.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WareHouseItems result = warehouseItemsService.addItem(sku, name, quantity, price, category, arrivalDate, supplier, warehouse, fileName, image);

        ArgumentCaptor<WareHouseItems> captor = ArgumentCaptor.forClass(WareHouseItems.class);
        verify(warehouseItemsRepository, times(1)).save(captor.capture());
        WareHouseItems savedItem = captor.getValue();

        assertEquals(sku, savedItem.getSku());
        assertEquals(name, savedItem.getName());
        assertEquals(quantity, savedItem.getQuantity());
        assertEquals(price, savedItem.getPrice());
        assertEquals(category, savedItem.getCategory());
        assertEquals(LocalDate.parse(arrivalDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")), savedItem.getArrivalDate());
        assertEquals(supplier, savedItem.getSupplier());
        assertEquals(warehouse, savedItem.getWarehouse());
        assertEquals(fileName, savedItem.getFileName());
        assertArrayEquals(image.getBytes(), savedItem.getImage());

        assertEquals(savedItem, result);
    }

    @Test
    void testAddItem_throwsExceptionWhenImageReadFails() throws IOException {
        String sku = "ORD123";
        String name = "Laptop";
        int quantity = 10;
        double price = 9999.99;
        String category = "Electronics";
        String arrivalDate = "2024-11-10";
        String supplier = "Tech Supplier";
        String warehouse = "Main Warehouse";
        String fileName = "laptop.png";
        MultipartFile image = mock(MultipartFile.class);

        when(image.getBytes()).thenThrow(new IOException("Error reading an image"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            warehouseItemsService.addItem(sku, name, quantity, price, category, arrivalDate, supplier, warehouse, fileName, image);
        });

        assertEquals("Error reading an image", exception.getCause().getMessage());

        verify(warehouseItemsRepository, never()).save(any(WareHouseItems.class));
    }

    @Test
    void testGetImage_ItemExists() {
        long itemId = item1.getId();

        when(warehouseItemsRepository.findById(itemId)).thenReturn(Optional.of(item1));
        ResponseEntity<byte[]> response = warehouseItemsService.getImage(itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(item1.getImage(), response.getBody());
    }

    @Test
    void testGetImage_ItemNotFound() {
        long itemId = item1.getId();

        when(warehouseItemsRepository.findById(itemId)).thenReturn(Optional.empty());
        ResponseEntity<byte[]> response = warehouseItemsService.getImage(itemId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateItem_ItemExists() {
        String id = "33";
        String sku = "Update ORD123";
        String name = "Updated Laptop";
        int quantity = 55;
        double price = 1999;
        String category = "Updated Electronics";
        String arrivalDate = "2024-11-12";
        String supplier = "Updated Supplier";
        String warehouse = "Updated Warehouse";
        String fileName = "item 1";
        MockMultipartFile image = new MockMultipartFile("image", "item1.jpg", "image/jpeg", "image data".getBytes());

        when(warehouseItemsRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(item1));
        when(warehouseItemsRepository.save(any(WareHouseItems.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WareHouseItems updatedItem = warehouseItemsService.updateItem(id, sku, name, quantity, price, category, arrivalDate, supplier, warehouse, fileName, image);

        verify(warehouseItemsRepository, times(1)).findById(Long.parseLong(id));

        ArgumentCaptor<WareHouseItems> captor = ArgumentCaptor.forClass(WareHouseItems.class);
        verify(warehouseItemsRepository, times(1)).save(captor.capture());

        WareHouseItems savedItem = captor.getValue();

        assertEquals(sku, savedItem.getSku());
        assertEquals(name, savedItem.getName());
        assertEquals(quantity, savedItem.getQuantity());
        assertEquals(price, savedItem.getPrice());
        assertEquals(category, savedItem.getCategory());
        assertEquals(LocalDate.parse(arrivalDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")), savedItem.getArrivalDate());
        assertEquals(supplier, savedItem.getSupplier());
        assertEquals(warehouse, savedItem.getWarehouse());

        assertEquals(savedItem, updatedItem);
    }

    @Test
    void testUpdateItem_ItemNotFound() {
        String id = "11";
        String order_id = "ORD123";
        String name = "Nonexistent Item";
        int quantity = 10;
        double price = 500.0;
        String category = "Nonexistent Category";
        String arrivalDate = "2024-11-10";
        String supplier = "Nonexistent Supplier";
        String warehouse = "Nonexistent Warehouse";
        String fileName = "item 1";
        MockMultipartFile image = new MockMultipartFile("image", "item1.jpg", "image/jpeg", "image data".getBytes());

        when(warehouseItemsRepository.findById(Long.parseLong(id))).thenReturn(Optional.empty());

        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> warehouseItemsService.updateItem(id, order_id, name, quantity, price, category, arrivalDate, supplier, warehouse, fileName, image)
        );

        assertEquals("Item not found with id: " + id, thrownException.getMessage());

        verify(warehouseItemsRepository, never()).save(any(WareHouseItems.class));
    }

    @Test
    public void testDeleteProductById_ProductExist() {
        int productId = 1;

        when(warehouseItemsRepository.findById(productId)).thenReturn(Optional.of(item1));

        ResponseEntity<String> response = warehouseItemsService.deleteProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product with ID: " + productId + " has been deleted", response.getBody());

        verify(warehouseItemsRepository, times(1)).deleteById(productId);
    }

    @Test
    public void testDeleteProductById_ProductNegativeNumber() {
        int productId = -1;

        ResponseEntity<String> response = warehouseItemsService.deleteProductById(productId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid product ID: must be a positive integer", response.getBody());

        verify(warehouseItemsRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testDeleteProductById_ProductNotFound() {
        int productId = 200;

        when(warehouseItemsRepository.findById(productId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = warehouseItemsService.deleteProductById(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product with ID: " + productId + " not found", response.getBody());

        verify(warehouseItemsRepository, never()).deleteById(anyLong());
    }
}
