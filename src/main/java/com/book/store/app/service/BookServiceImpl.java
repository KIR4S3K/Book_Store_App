package com.book.store.app.service;

import static com.book.store.app.specification.BookSpecification.withSearchParams;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.BookSearchParametersDto;
import com.book.store.app.dto.CreateBookRequestDto;
import com.book.store.app.entity.Book;
import com.book.store.app.exception.EntityNotFoundException;
import com.book.store.app.mapper.BookMapper;
import com.book.store.app.repository.BookRepository;
import java.util.List;
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
        return bookRepository.findByDeletedFalse().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + id));
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public BookDto update(Long id, CreateBookRequestDto dto) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + id));
        bookMapper.updateEntityFromDto(dto, book);
        Book updated = bookRepository.save(book);
        return bookMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + id));
        bookRepository.deleteById(book.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> search(BookSearchParametersDto params) {
        return bookRepository.findAll(withSearchParams(params)).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
