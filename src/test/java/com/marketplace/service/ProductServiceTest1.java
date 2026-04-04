package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest1 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    private Product laptop;

    @BeforeEach
    void setUp() {
        laptop = new Product();
        laptop.setName("Laptop Pro");
        laptop.setPrice(999.99);
        laptop.setCategory("Electronics");
        laptop.setStock(10);
    }

    // UNIT TEST 1: findAll returns all products
    @Test
    void findAll_shouldReturnAllProducts() {
        Product book = new Product();
        book.setName("Clean Code");
        when(repo.findAll()).thenReturn(List.of(laptop, book));

        List<Product> result = service.findAll();

        assertEquals(2, result.size());
        verify(repo).findAll();
    }

    // UNIT TEST 2: save persists product
    @Test
    void save_shouldPersistAndReturnProduct() {
        when(repo.save(laptop)).thenReturn(laptop);

        Product saved = service.save(laptop);

        assertNotNull(saved);
        assertEquals("Laptop Pro", saved.getName());
        verify(repo).save(laptop);
    }

    // UNIT TEST 3: deleteById calls repository — username + admin flag required
    @Test
    void deleteById_shouldCallRepository() {
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(laptop));
        doNothing().when(repo).deleteById(1L);

        service.deleteById(1L, "admin", true);

        verify(repo).deleteById(1L);
    }

    // UNIT TEST 4: deleteById throws when product not found
    @Test
    void deleteById_shouldThrow_whenProductNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteById(99L, "admin", true));

        verify(repo, never()).deleteById(any());
    }

    // UNIT TEST 5: findById returns correct product
    @Test
    void findById_shouldReturnProduct_whenExists() {
        when(repo.findById(1L)).thenReturn(Optional.of(laptop));

        Optional<Product> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Laptop Pro", result.get().getName());
    }
}
