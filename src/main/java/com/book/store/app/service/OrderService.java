package com.book.store.app.service;

import com.book.store.app.dto.OrderItemResponseDto;
import com.book.store.app.dto.OrderRequestDto;
import com.book.store.app.dto.OrderResponseDto;
import java.util.List;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto request);

    List<OrderResponseDto> getOrderHistory();

    List<OrderItemResponseDto> getOrderItems(Long orderId);

    OrderItemResponseDto getOrderItem(Long orderId, Long itemId);

    OrderResponseDto updateOrderStatus(Long orderId, String status);
}
