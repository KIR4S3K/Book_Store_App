package com.book.store.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {

    @Mock
    private ShoppingCartRepository cartRepo;

    @Mock
    private CartItemRepository itemRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private BookRepository bookRepo;

    private ShoppingCartServiceImpl service;
    private CartMapper cartMapper;

    private final String email = "user@example.com";
    private User user;
    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        // Use a real CartMapper instance
        cartMapper = new CartMapper();
        service = new ShoppingCartServiceImpl(cartRepo, itemRepo, userRepo, bookRepo, cartMapper);

        user = new User();
        user.setId(1L);
        user.setEmail(email);

        cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(user);
    }

    private void mockSecurityContextWithEmail(String email) {
        // Use UsernamePasswordAuthenticationToken instead of mocking Authentication interface
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("getCartForCurrentUser returns ShoppingCartDto for logged user")
    void getCartForCurrentUser_shouldReturnDto() {
        mockSecurityContextWithEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));

        ShoppingCartDto result = service.getCartForCurrentUser();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(cart.getId());
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getCartItems()).isEmpty();

        verify(userRepo).findByEmail(email);
        verify(cartRepo).findByUser(user);
    }

    @Test
    @DisplayName("getCartForCurrentUser throws when user not found")
    void getCartForCurrentUser_whenUserNotFound_shouldThrowException() {
        mockSecurityContextWithEmail(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.getCartForCurrentUser()
        );

        assertThat(exception.getMessage()).contains("User not found");
        verify(userRepo).findByEmail(email);
        verifyNoInteractions(cartRepo);
    }

    @Test
    @DisplayName("addToCart adds new item to cart")
    void addToCart_shouldAddItem() {
        mockSecurityContextWithEmail(email);

        final Long bookId = 1L;
        final int quantity = 2;

        AddToCartRequestDto request = new AddToCartRequestDto();
        request.setBookId(bookId);
        request.setQuantity(quantity);

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Sample Title");

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        when(itemRepo.save(any(CartItem.class)))
                .thenAnswer(invocation -> {
                    CartItem ci = invocation.getArgument(0);
                    ci.setId(100L); // simulate ID assignment
                    return ci;
                });

        CartItemDto result = service.addToCart(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getBookId()).isEqualTo(bookId);
        assertThat(result.getBookTitle()).isEqualTo("Sample Title");
        assertThat(result.getQuantity()).isEqualTo(quantity);

        verify(userRepo).findByEmail(email);
        verify(cartRepo).findByUser(user);
        verify(bookRepo).findById(bookId);
        verify(itemRepo).save(any(CartItem.class));
    }

    @Test
    @DisplayName("updateCartItem updates quantity")
    void updateCartItem_shouldUpdateQuantity() {
        mockSecurityContextWithEmail(email);

        final Long cartItemId = 1L;
        final int newQuantity = 5;

        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(newQuantity);

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Updated Title");

        CartItem item = new CartItem();
        item.setId(cartItemId);
        item.setBook(book);
        item.setQuantity(1);

        when(itemRepo.findById(cartItemId)).thenReturn(Optional.of(item));
        when(itemRepo.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartItemDto result = service.updateCartItem(cartItemId, request);

        assertThat(result).isNotNull();
        assertThat(item.getQuantity()).isEqualTo(newQuantity);
        assertThat(result.getQuantity()).isEqualTo(newQuantity);
        assertThat(result.getBookId()).isEqualTo(book.getId());
        assertThat(result.getBookTitle()).isEqualTo(book.getTitle());

        verify(itemRepo).findById(cartItemId);
        verify(itemRepo).save(item);
    }

    @Test
    @DisplayName("updateCartItem throws when item not found")
    void updateCartItem_whenItemNotFound_shouldThrowException() {
        mockSecurityContextWithEmail(email);

        final Long cartItemId = 1L;
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(3);

        when(itemRepo.findById(cartItemId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.updateCartItem(cartItemId, request)
        );

        assertThat(exception.getMessage()).contains("CartItem not found");
        verify(itemRepo).findById(cartItemId);
        verify(itemRepo, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("removeCartItem removes item from cart")
    void removeCartItem_shouldRemoveItem() {
        mockSecurityContextWithEmail(email);

        final Long itemId = 1L;
        CartItem item = new CartItem();
        item.setId(itemId);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(itemRepo).delete(item);

        service.removeCartItem(itemId);

        verify(itemRepo).findById(itemId);
        verify(itemRepo).delete(item);
    }

    @Test
    @DisplayName("removeCartItem throws when item not found")
    void removeCartItem_whenItemNotFound_shouldThrowException() {
        mockSecurityContextWithEmail(email);

        final Long itemId = 1L;
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.removeCartItem(itemId)
        );

        assertThat(exception.getMessage()).contains("CartItem not found");
        verify(itemRepo).findById(itemId);
        verify(itemRepo, never()).delete(any(CartItem.class));
    }
}
