package com.marketplace.controller;

import com.marketplace.entity.Product;
import com.marketplace.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * INTEGRATION TEST 1: GET /products — view, model attributes, authentication
 */
@WebMvcTest(ProductController.class)
public class ProductControllerTest1 {

    @Autowired MockMvc mockMvc;
    @MockBean ProductService service;

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getProducts_shouldReturn200AndProductsView() throws Exception {
        Product p = new Product();
        p.setName("Test Laptop");
        p.setPrice(999.0);
        p.setCategory("Electronics");
        p.setStock(5);

        when(service.findAll()).thenReturn(List.of(p));
        when(service.count()).thenReturn(1L);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("totalCount", 1L));

        verify(service).findAll();
        verify(service).count();
    }

    @Test
    void getProducts_withoutLogin_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().is3xxRedirection());
    }
}
