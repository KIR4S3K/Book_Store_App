package com.book.store.app.mapper;

import com.book.store.app.dto.OrderItemResponseDto;
import com.book.store.app.dto.OrderResponseDto;
import com.book.store.app.entity.Order;
import com.book.store.app.entity.OrderItem;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponseDto toDto(Order order) {
        List<OrderItemResponseDto> items = order.getOrderItems().stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
        return new OrderResponseDto(
                order.getId(),
                order.getUser().getId(),
                items,
                order.getOrderDate(),
                order.getTotal(),
                order.getStatus().name(),
                order.getShippingAddress()
        );
    }

    public OrderItemResponseDto toItemDto(OrderItem item) {
        return new OrderItemResponseDto(
                item.getId(),
                item.getBook().getId(),
                item.getQuantity()
        );
    }

}
