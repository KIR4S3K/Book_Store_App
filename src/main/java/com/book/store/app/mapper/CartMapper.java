package com.book.store.app.mapper;

import com.book.store.app.dto.CartItemDto;
import com.book.store.app.dto.ShoppingCartDto;
import com.book.store.app.entity.CartItem;
import com.book.store.app.entity.ShoppingCart;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public CartItemDto toDto(CartItem item) {
        return new CartItemDto(
                item.getId(),
                item.getBook().getId(),
                item.getBook().getTitle(),
                item.getQuantity()
        );
    }

    public ShoppingCartDto toDto(ShoppingCart cart) {
        return new ShoppingCartDto(
                cart.getId(),
                cart.getUser().getId(),
                cart.getCartItems().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
}
