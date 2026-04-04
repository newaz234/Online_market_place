package com.marketplace.service;

import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest15 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void count_shouldReturnTotalProductCount() {
        when(repo.count()).thenReturn(7L);

        long count = service.count();

        assertEquals(7L, count);
        verify(repo).count();
    }
}
