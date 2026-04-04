package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest13 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void findInStock_shouldReturnOnlyProductsWithStock() {
        Product p1 = new Product(); p1.setStock(5);
        Product p2 = new Product(); p2.setStock(50);
        when(repo.findByStockGreaterThan(0)).thenReturn(List.of(p1, p2));

        List<Product> result = service.findInStock();

        assertEquals(2, result.size());
        verify(repo).findByStockGreaterThan(0);
    }
}
