package com.book.store.app.service;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.CreateBookRequestDto;
import com.book.store.app.entity.Book;
import com.book.store.app.exception.EntityNotFoundException;
import com.book.store.app.mapper.BookMapper;
import com.book.store.app.repository.BookRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookDto save(CreateBookRequestDto dto) {
        Book entity = bookMapper.toEntity(dto);
        Book saved = bookRepository.save(entity);
        return bookMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id "
                        + id));
        return bookMapper.toDto(book);
    }
}
