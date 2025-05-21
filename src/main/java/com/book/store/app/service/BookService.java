package com.book.store.app.service;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto dto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto update(Long id, CreateBookRequestDto dto);

    void delete(Long id);
}
