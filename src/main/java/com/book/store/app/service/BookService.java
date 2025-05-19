package com.book.store.app.service;

import com.book.store.app.entity.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();

    Book findById(Long id);
}
