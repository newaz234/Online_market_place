package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest14 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void update_asAdmin_shouldModifyAllFields() {
        Product existing = new Product();
        existing.setName("Old Name"); existing.setPrice(10.0);
        existing.setCategory("Books"); existing.setStock(2);

        Product updated = new Product();
        updated.setName("New Name"); updated.setPrice(99.0);
        updated.setCategory("Electronics"); updated.setStock(20);
        updated.setDescription("Updated desc");

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = service.update(1L, updated, "admin", true);

        assertEquals("New Name", result.getName());
        assertEquals(99.0, result.getPrice());
        assertEquals("Electronics", result.getCategory());
        assertEquals(20, result.getStock());
        assertEquals("Updated desc", result.getDescription());
        verify(repo).save(any());
    }
}
