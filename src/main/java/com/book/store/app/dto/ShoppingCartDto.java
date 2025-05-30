package com.book.store.app.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> cartItems;
}
