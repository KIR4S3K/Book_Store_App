package com.book.store.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.book.store.app.entity.Book;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Should save and retrieve a Book entity")
    void saveAndFindById() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("John Doe");
        book.setIsbn("ISBN-1111");
        book.setPrice(new BigDecimal("49.99"));
        book.setDescription("Description...");
        book.setCoverImage("http://example.com/cover.jpg");

        Book saved = bookRepository.save(book);

        Optional<Book> foundOpt = bookRepository.findByIdAndDeletedFalse(saved.getId());
        assertThat(foundOpt).isPresent();
        Book found = foundOpt.get();
        assertThat(found.getTitle()).isEqualTo("Test Book");
        assertThat(found.getAuthor()).isEqualTo("John Doe");
        assertThat(found.getIsbn()).isEqualTo("ISBN-1111");
        assertThat(found.getPrice()).isEqualByComparingTo("49.99");
    }

    @Test
    @DisplayName("When entity is marked as deleted, findByIdAndDeletedFalse(...) "
            + "returns empty Optional")
    void softDelete() {
        Book book = new Book();
        book.setTitle("SoftDelete Book");
        book.setAuthor("Jane Smith");
        book.setIsbn("ISBN-2222");
        book.setPrice(new BigDecimal("29.99"));
        book.setDeleted(true);

        Book saved = bookRepository.save(book);
        Optional<Book> foundOpt = bookRepository.findByIdAndDeletedFalse(saved.getId());
        assertThat(foundOpt).isEmpty();
    }
}
