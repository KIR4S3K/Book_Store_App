package com.book.store.app.service;

import com.book.store.app.dto.AddToCartRequestDto;
import com.book.store.app.dto.CartItemDto;
import com.book.store.app.dto.ShoppingCartDto;
import com.book.store.app.dto.UpdateCartItemRequestDto;

public interface ShoppingCartService {
    ShoppingCartDto getCartForCurrentUser();

    CartItemDto addToCart(AddToCartRequestDto request);

    CartItemDto updateCartItem(Long cartItemId, UpdateCartItemRequestDto request);

    void removeCartItem(Long cartItemId);
}
