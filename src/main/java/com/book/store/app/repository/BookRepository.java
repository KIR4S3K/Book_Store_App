package com.book.store.app.repository;

import com.book.store.app.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository
        extends JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book> {

    List<Book> findByDeletedFalse();

    Optional<Book> findByIdAndDeletedFalse(Long id);

}
