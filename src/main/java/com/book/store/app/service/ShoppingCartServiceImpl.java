package com.book.store.app.service;

import com.book.store.app.dto.AddToCartRequestDto;
import com.book.store.app.dto.CartItemDto;
import com.book.store.app.dto.ShoppingCartDto;
import com.book.store.app.dto.UpdateCartItemRequestDto;
import com.book.store.app.entity.Book;
import com.book.store.app.entity.CartItem;
import com.book.store.app.entity.ShoppingCart;
import com.book.store.app.entity.User;
import com.book.store.app.exception.EntityNotFoundException;
import com.book.store.app.mapper.CartMapper;
import com.book.store.app.repository.BookRepository;
import com.book.store.app.repository.CartItemRepository;
import com.book.store.app.repository.ShoppingCartRepository;
import com.book.store.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final UserRepository userRepo;
    private final BookRepository bookRepo;
    private final CartMapper mapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: "
                        + email));
    }

    private ShoppingCart getOrCreateCart(User user) {
        return cartRepo.findByUser(user)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setUser(user);
                    return cartRepo.save(cart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCartForCurrentUser() {
        User user = getCurrentUser();
        ShoppingCart cart = getOrCreateCart(user);
        return mapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartItemDto addToCart(AddToCartRequestDto request) {
        User user = getCurrentUser();
        ShoppingCart cart = getOrCreateCart(user);

        Book book = bookRepo.findById(request.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found: "
                        + request.getBookId()));

        CartItem existing = cart.getCartItems().stream()
                .filter(i -> i.getBook().getId().equals(book.getId()))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            itemRepo.save(existing);
            return mapper.toDto(existing);
        }

        CartItem item = new CartItem();
        item.setShoppingCart(cart);
        item.setBook(book);
        item.setQuantity(request.getQuantity());
        cart.getCartItems().add(item);
        CartItem saved = itemRepo.save(item);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public CartItemDto updateCartItem(Long cartItemId, UpdateCartItemRequestDto request) {
        CartItem item = itemRepo.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found: "
                        + cartItemId));
        item.setQuantity(request.getQuantity());
        return mapper.toDto(itemRepo.save(item));
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId) {
        CartItem item = itemRepo.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found: "
                        + cartItemId));
        itemRepo.delete(item);
    }
}
