package com.book.store.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.book.store.app.dto.AddToCartRequestDto;
import com.book.store.app.dto.CartItemDto;
import com.book.store.app.dto.ShoppingCartDto;
import com.book.store.app.dto.UpdateCartItemRequestDto;
import com.book.store.app.entity.Book;
import com.book.store.app.entity.CartItem;
import com.book.store.app.entity.ShoppingCart;
import com.book.store.app.entity.User;
import com.book.store.app.mapper.CartMapper;
import com.book.store.app.repository.BookRepository;
import com.book.store.app.repository.CartItemRepository;
import com.book.store.app.repository.ShoppingCartRepository;
import com.book.store.app.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
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
    @Mock
    private CartMapper mapper;

    @InjectMocks
    private ShoppingCartServiceImpl service;

    private final String email = "user@example.com";
    private User user;
    private ShoppingCart cart;
    private ShoppingCartDto dto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail(email);

        cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(user);

        dto = new ShoppingCartDto(1L, 1L, List.of());
    }

    private void mockSecurityContextWithEmail(String email) {
        Authentication auth = mock(Authentication.class, withSettings().lenient());
        when(auth.getName()).thenReturn(email);

        SecurityContext context = mock(SecurityContext.class, withSettings().lenient());
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("getCartForCurrentUser returns ShoppingCartDto for logged user")
    void getCartForCurrentUser_shouldReturnDto() {
        mockSecurityContextWithEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(mapper.toDto(cart)).thenReturn(dto);

        ShoppingCartDto result = service.getCartForCurrentUser();

        assertThat(result).isEqualTo(dto);
        verify(userRepo).findByEmail(email);
        verify(cartRepo).findByUser(user);
        verify(mapper).toDto(cart);
    }

    @Test
    @DisplayName("getCartForCurrentUser throws when user not found")
    void getCartForCurrentUser_whenUserNotFound_shouldThrowException() {
        mockSecurityContextWithEmail(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.getCartForCurrentUser());

        assertThat(exception).hasMessageContaining("User not found");
        verify(userRepo).findByEmail(email);
        verifyNoInteractions(cartRepo, mapper);
    }

    @Test
    @DisplayName("addToCart adds new item to cart")
    void addToCart_shouldAddItem() {
        mockSecurityContextWithEmail(email);

        Long bookId = 1L;
        int quantity = 2;

        AddToCartRequestDto request = new AddToCartRequestDto();
        request.setBookId(bookId);
        request.setQuantity(quantity);

        Book book = new Book();
        book.setId(bookId);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        when(itemRepo.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartItemDto cartItemDto = new CartItemDto(1L, bookId,
                "Sample Book Title", quantity);
        when(mapper.toDto(any(CartItem.class))).thenReturn(cartItemDto);

        CartItemDto result = service.addToCart(request);

        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(bookId);
        assertThat(result.getQuantity()).isEqualTo(quantity);

        verify(userRepo).findByEmail(email);
        verify(cartRepo).findByUser(user);
        verify(bookRepo).findById(bookId);
        verify(itemRepo).save(any(CartItem.class));
        verify(mapper).toDto(any(CartItem.class));
    }

    @Test
    @DisplayName("updateCartItem updates quantity")
    void updateCartItem_shouldUpdateQuantity() {
        mockSecurityContextWithEmail(email);

        Long cartItemId = 1L;
        int newQuantity = 5;

        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(newQuantity);

        CartItem item = new CartItem();
        item.setId(cartItemId);
        item.setQuantity(1);

        when(itemRepo.findById(cartItemId)).thenReturn(Optional.of(item));
        when(itemRepo.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartItemDto cartItemDto = new CartItemDto(1L, 1L,
                "Sample Book Title", newQuantity);
        when(mapper.toDto(any(CartItem.class))).thenReturn(cartItemDto);

        CartItemDto result = service.updateCartItem(cartItemId, request);

        assertThat(result).isNotNull();
        assertThat(item.getQuantity()).isEqualTo(newQuantity);

        verify(itemRepo).findById(cartItemId);
        verify(itemRepo).save(item);
        verify(mapper).toDto(item);
    }

    @Test
    @DisplayName("removeCartItem removes item from cart")
    void removeCartItem_shouldRemoveItem() {
        mockSecurityContextWithEmail(email);

        Long itemId = 1L;
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

        Long itemId = 1L;
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.removeCartItem(itemId));

        assertThat(exception).hasMessageContaining("CartItem not found");

        verify(itemRepo).findById(itemId);
        verify(itemRepo, never()).delete(any());
    }
}
