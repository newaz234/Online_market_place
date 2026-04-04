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
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * INTEGRATION TEST 3: DELETE, edit form, inStock filter
 */
@WebMvcTest(ProductController.class)
public class ProductControllerTest3 {

    @Autowired MockMvc mockMvc;
    @MockBean ProductService service;

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void deleteProduct_shouldCallServiceAndRedirect() throws Exception {
        // deleteById now takes (Long, String, boolean)
        doNothing().when(service).deleteById(anyLong(), anyString(), anyBoolean());

        mockMvc.perform(post("/products/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(service).deleteById(eq(1L), eq("seller1"), eq(false));
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void getEditForm_shouldReturnEditView_whenProductFound() throws Exception {
        Product p = new Product();
        p.setName("Editable Product");
        p.setPrice(49.0);
        // set seller so ownership check passes
        com.marketplace.entity.User seller = new com.marketplace.entity.User();
        seller.setUsername("seller1");
        p.setSeller(seller);

        when(service.findById(3L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/products/edit/3"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-product"))
                .andExpect(model().attributeExists("product"));

        verify(service).findById(3L);
    }

    @Test
    @WithMockUser
    void getProducts_withInStockFilter_shouldCallFindInStock() throws Exception {
        when(service.findInStock()).thenReturn(List.of());
        when(service.count()).thenReturn(0L);

        mockMvc.perform(get("/products").param("inStockOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"));

        verify(service).findInStock();
        verify(service, never()).findAll();
    }
}
