package com.book.store.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
