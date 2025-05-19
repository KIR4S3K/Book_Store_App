package com.book.store.app.repository;

import com.book.store.app.entity.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
