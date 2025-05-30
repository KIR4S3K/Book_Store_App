package com.book.store.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}
