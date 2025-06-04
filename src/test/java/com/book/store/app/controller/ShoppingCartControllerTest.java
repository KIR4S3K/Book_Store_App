package com.book.store.app.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.book.store.app.dto.AddToCartRequestDto;
import com.book.store.app.dto.CartItemDto;
import com.book.store.app.dto.ShoppingCartDto;
import com.book.store.app.dto.UpdateCartItemRequestDto;
import com.book.store.app.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/cart returns ShoppingCartDto (role=USER)")
    @WithMockUser(roles = "USER")
    void getCart_asUser() throws Exception {
        CartItemDto item = new CartItemDto(10L, 20L, "Sample Book", 2);
        ShoppingCartDto dto = new ShoppingCartDto(1L, 5L, List.of(item));

        Mockito.when(cartService.getCartForCurrentUser()).thenReturn(dto);

        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(5)))
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].id", is(10)))
                .andExpect(jsonPath("$.cartItems[0].bookId", is(20)))
                .andExpect(jsonPath("$.cartItems[0].bookTitle", is("Sample Book")))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(2)));
    }

    @Test
    @DisplayName("GET /api/cart without auth returns 401")
    void getCart_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/cart adds item to cart (role=USER)")
    @WithMockUser(roles = "USER")
    void addToCart_asUser() throws Exception {
        AddToCartRequestDto request = new AddToCartRequestDto();
        request.setBookId(20L);
        request.setQuantity(3);

        CartItemDto returned = new CartItemDto(15L, 20L, "Another Book", 3);
        Mockito.when(cartService.addToCart(any(AddToCartRequestDto.class)))
                .thenReturn(returned);

        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(15)))
                .andExpect(jsonPath("$.bookId", is(20)))
                .andExpect(jsonPath("$.bookTitle", is("Another Book")))
                .andExpect(jsonPath("$.quantity", is(3)));
    }

    @Test
    @DisplayName("POST /api/cart without auth returns 401")
    void addToCart_unauthenticated() throws Exception {
        AddToCartRequestDto request = new AddToCartRequestDto();
        request.setBookId(20L);
        request.setQuantity(1);

        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/cart/cart-items/{cartItemId} updates item (role=USER)")
    @WithMockUser(roles = "USER")
    void updateItem_asUser() throws Exception {
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(5);

        CartItemDto updated = new CartItemDto(10L, 20L, "Updated Book", 5);
        Mockito.when(cartService.updateCartItem(eq(10L), any(UpdateCartItemRequestDto.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/cart/cart-items/{cartItemId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.bookId", is(20)))
                .andExpect(jsonPath("$.bookTitle", is("Updated Book")))
                .andExpect(jsonPath("$.quantity", is(5)));
    }

    @Test
    @DisplayName("PUT /api/cart/cart-items/{cartItemId} without auth returns 401")
    void updateItem_unauthenticated() throws Exception {
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(2);

        mockMvc.perform(put("/api/cart/cart-items/{cartItemId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/cart/cart-items/{cartItemId} removes item (role=USER)")
    @WithMockUser(roles = "USER")
    void removeItem_asUser() throws Exception {
        Mockito.doNothing().when(cartService).removeCartItem(10L);

        mockMvc.perform(delete("/api/cart/cart-items/{cartItemId}", 10L))
                .andExpect(status().isNoContent());

        Mockito.verify(cartService).removeCartItem(10L);
    }

    @Test
    @DisplayName("DELETE /api/cart/cart-items/{cartItemId} without auth returns 401")
    void removeItem_unauthenticated() throws Exception {
        mockMvc.perform(delete("/api/cart/cart-items/{cartItemId}", 10L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/cart with role=ADMIN returns 403")
    @WithMockUser(roles = "ADMIN")
    void getCart_forbiddenForAdmin() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/cart with role=ADMIN returns 403")
    @WithMockUser(roles = "ADMIN")
    void addToCart_forbiddenForAdmin() throws Exception {
        AddToCartRequestDto request = new AddToCartRequestDto();
        request.setBookId(30L);
        request.setQuantity(1);

        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/cart/cart-items/{cartItemId} with role=ADMIN returns 403")
    @WithMockUser(roles = "ADMIN")
    void updateItem_forbiddenForAdmin() throws Exception {
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(4);

        mockMvc.perform(put("/api/cart/cart-items/{cartItemId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/cart/cart-items/{cartItemId} with role=ADMIN returns 403")
    @WithMockUser(roles = "ADMIN")
    void removeItem_forbiddenForAdmin() throws Exception {
        mockMvc.perform(delete("/api/cart/cart-items/{cartItemId}", 10L))
                .andExpect(status().isForbidden());
    }
}
