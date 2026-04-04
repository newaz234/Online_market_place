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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * INTEGRATION TEST 2: POST /products, search, category filter
 */
@WebMvcTest(ProductController.class)
public class ProductControllerTest2 {

    @Autowired MockMvc mockMvc;
    @MockBean ProductService service;

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void postProduct_shouldSaveAndRedirect() throws Exception {
        when(service.saveForSeller(any(Product.class), eq("seller1"))).thenReturn(new Product());

        mockMvc.perform(post("/products")
                .with(csrf())
                .param("name", "New Keyboard")
                .param("price", "79.99")
                .param("category", "Electronics")
                .param("stock", "15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(service).saveForSeller(any(Product.class), eq("seller1"));
    }

    @Test
    @WithMockUser(roles = {"BUYER"})
    void postProduct_asBuyer_shouldBeForbidden() throws Exception {
        mockMvc.perform(post("/products")
                .with(csrf())
                .param("name", "Hack Product")
                .param("price", "1.0"))
                .andExpect(status().isForbidden());

        verify(service, never()).save(any());
        verify(service, never()).saveForSeller(any(), any());
    }

    @Test
    @WithMockUser
    void getProducts_withSearch_shouldCallSearchMethod() throws Exception {
        when(service.search("keyboard")).thenReturn(List.of());
        when(service.count()).thenReturn(0L);

        mockMvc.perform(get("/products").param("search", "keyboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("search", "keyboard"));

        verify(service).search("keyboard");
        verify(service, never()).findAll();
    }
}
