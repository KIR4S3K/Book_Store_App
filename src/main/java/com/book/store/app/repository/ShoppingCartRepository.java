package com.book.store.app.repository;

import com.book.store.app.entity.ShoppingCart;
import com.book.store.app.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}
