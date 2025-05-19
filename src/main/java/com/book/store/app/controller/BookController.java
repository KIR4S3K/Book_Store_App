package com.book.store.app.controller;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.CreateBookRequestDto;
import com.book.store.app.entity.Book;
import com.book.store.app.mapper.BookMapper;
import com.book.store.app.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping
    public List<BookDto> getAll() {
        return bookMapper.toDtoList(bookService.findAll());
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookMapper.toDto(bookService.findById(id));
    }

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto requestDto) {
        Book saved = bookService.save(bookMapper.toEntity(requestDto));
        return bookMapper.toDto(saved);
    }
}
