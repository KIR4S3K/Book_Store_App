package com.book.store.app.service;

import com.book.store.app.dto.OrderItemResponseDto;
import com.book.store.app.dto.OrderRequestDto;
import com.book.store.app.dto.OrderResponseDto;
import com.book.store.app.entity.Order;
import com.book.store.app.entity.OrderItem;
import com.book.store.app.entity.OrderStatus;
import com.book.store.app.entity.ShoppingCart;
import com.book.store.app.entity.User;
import com.book.store.app.exception.EntityNotFoundException;
import com.book.store.app.mapper.OrderMapper;
import com.book.store.app.repository.BookRepository;
import com.book.store.app.repository.OrderItemRepository;
import com.book.store.app.repository.OrderRepository;
import com.book.store.app.repository.ShoppingCartRepository;
import com.book.store.app.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final UserRepository userRepo;
    private final ShoppingCartRepository cartRepo;
    private final BookRepository bookRepo;
    private final OrderMapper mapper;
    private final Clock clock;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto request) {
        User user = getCurrentUser();
        final ShoppingCart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user"));
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now(clock));
        order.setShippingAddress(request.getShippingAddress());
        cart.getCartItems().forEach(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setBook(ci.getBook());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getBook().getPrice());
            order.getOrderItems().add(oi);
        });
        BigDecimal total = order.getOrderItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        cart.getCartItems().clear();
        cartRepo.save(cart);
        Order saved = orderRepo.save(order);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrderHistory() {
        User user = getCurrentUser();
        return orderRepo.findAllByUserId(user.getId()).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItems(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(getCurrentUser().getId())) {
            throw new EntityNotFoundException("Order not found: " + orderId);
        }
        return itemRepo.findAllByOrderId(orderId).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItem(Long orderId, Long itemId) {
        OrderItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + itemId));
        if (!item.getOrder().getId().equals(orderId)) {
            throw new EntityNotFoundException("Item not found in order: " + itemId);
        }
        return mapper.toItemDto(item);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, String status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        order.setStatus(OrderStatus.valueOf(status));
        Order updated = orderRepo.save(order);
        return mapper.toDto(updated);
    }
}
