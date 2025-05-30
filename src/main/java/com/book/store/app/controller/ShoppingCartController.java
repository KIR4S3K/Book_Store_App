package com.book.store.app.controller;

import com.book.store.app.dto.AddToCartRequestDto;
import com.book.store.app.dto.CartItemDto;
import com.book.store.app.dto.ShoppingCartDto;
import com.book.store.app.dto.UpdateCartItemRequestDto;
import com.book.store.app.service.ShoppingCartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto getCart() {
        return cartService.getCartForCurrentUser();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public CartItemDto addToCart(@Valid @RequestBody AddToCartRequestDto request) {
        return cartService.addToCart(request);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public CartItemDto updateItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDto request
    ) {
        return cartService.updateCartItem(cartItemId, request);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER')")
    public void removeItem(@PathVariable Long cartItemId) {
        cartService
                .removeCartItem(cartItemId);
    }
}
