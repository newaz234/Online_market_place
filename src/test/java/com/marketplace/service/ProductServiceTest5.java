package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest5 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void deleteById_asAdmin_shouldDeleteSuccessfully() {
        Product p = new Product();
        p.setName("Book");
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        doNothing().when(repo).deleteById(1L);

        service.deleteById(1L, "admin", true);

        verify(repo).deleteById(1L);
    }
}
