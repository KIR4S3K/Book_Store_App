package com.book.store.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.BookSearchParametersDto;
import com.book.store.app.dto.CreateBookRequestDto;
import com.book.store.app.entity.Book;
import com.book.store.app.exception.EntityNotFoundException;
import com.book.store.app.mapper.BookMapper;
import com.book.store.app.repository.BookRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book exampleBook;
    private CreateBookRequestDto createDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        exampleBook = new Book();
        exampleBook.setId(1L);
        exampleBook.setTitle("Title");
        exampleBook.setAuthor("Author");
        exampleBook.setIsbn("ISBN-123");
        exampleBook.setPrice(new BigDecimal("19.99"));

        createDto = new CreateBookRequestDto();
        createDto.setTitle("New Title");
        createDto.setAuthor("New Author");
        createDto.setIsbn("ISBN-456");
        createDto.setPrice(new BigDecimal("29.99"));
    }

    @Test
    @DisplayName("Should save a new book")
    void save_newBook() {
        Book toSave = new Book();
        // mapper creates an entity from DTO
        when(bookMapper.toEntity(createDto)).thenReturn(toSave);
        when(bookRepository.save(toSave)).thenReturn(exampleBook);
        when(bookMapper.toDto(exampleBook)).thenReturn(new BookDto(
                exampleBook.getId(),
                exampleBook.getTitle(),
                exampleBook.getAuthor(),
                exampleBook.getIsbn(),
                exampleBook.getPrice(),
                exampleBook.getDescription(),
                exampleBook.getCoverImage(),
                null
        ));

        BookDto result = bookService.save(createDto);

        assertThat(result.getId()).isEqualTo(1L);
        verify(bookMapper).toEntity(createDto);
        verify(bookRepository).save(toSave);
        verify(bookMapper).toDto(exampleBook);
    }

    @Test
    @DisplayName("findAll returns a paginated list of BookDto")
    void findAll_booksPage() {
        Page<Book> bookPage = new PageImpl<>(List.of(exampleBook));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.toDto(exampleBook)).thenReturn(new BookDto(
                exampleBook.getId(),
                exampleBook.getTitle(),
                exampleBook.getAuthor(),
                exampleBook.getIsbn(),
                exampleBook.getPrice(),
                exampleBook.getDescription(),
                exampleBook.getCoverImage(),
                null
        ));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));
        Page<BookDto> result = bookService.findAll(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(exampleBook);
    }

    @Test
    @DisplayName("findById correctly returns BookDto when entity exists")
    void findById_exists() {
        when(bookRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(exampleBook));
        when(bookMapper.toDto(exampleBook)).thenReturn(new BookDto(
                exampleBook.getId(),
                exampleBook.getTitle(),
                exampleBook.getAuthor(),
                exampleBook.getIsbn(),
                exampleBook.getPrice(),
                exampleBook.getDescription(),
                exampleBook.getCoverImage(),
                null
        ));

        BookDto result = bookService.findById(1L);
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookRepository).findByIdAndDeletedFalse(1L);
        verify(bookMapper).toDto(exampleBook);
    }

    @Test
    @DisplayName("findById throws EntityNotFoundException when entity is missing")
    void findById_notFound() {
        when(bookRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(2L));
    }

    @Test
    @DisplayName("update correctly updates and returns BookDto")
    void update_existing() {
        when(bookRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(exampleBook));
        // simulate mapper updating entity
        doAnswer(invocation -> {
            CreateBookRequestDto dto = invocation.getArgument(0);
            Book b = invocation.getArgument(1);
            b.setTitle(dto.getTitle());
            return null;
        }).when(bookMapper).updateEntityFromDto(eq(createDto), any(Book.class));

        when(bookRepository.save(exampleBook)).thenReturn(exampleBook);
        when(bookMapper.toDto(exampleBook)).thenReturn(new BookDto(
                exampleBook.getId(), createDto.getTitle(), exampleBook.getAuthor(),
                exampleBook.getIsbn(), exampleBook.getPrice(), exampleBook.getDescription(),
                exampleBook.getCoverImage(), null
        ));

        BookDto result = bookService.update(1L, createDto);
        assertThat(result.getTitle()).isEqualTo("New Title");
        verify(bookRepository).findByIdAndDeletedFalse(1L);
        verify(bookMapper).updateEntityFromDto(createDto, exampleBook);
    }

    @Test
    @DisplayName("update throws exception when book is missing")
    void update_notFound() {
        when(bookRepository.findByIdAndDeletedFalse(5L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookService.update(5L, createDto));
    }

    @Test
    @DisplayName("delete correctly performs soft delete on entity")
    void delete_existing() {
        when(bookRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(exampleBook));
        doNothing().when(bookRepository).deleteById(1L);

        bookService.delete(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws exception when entity is missing")
    void delete_notFound() {
        when(bookRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookService.delete(10L));
    }

    @Test
    @DisplayName("search returns a page of BookDto according to parameters")
    void search_withParams() {
        Page<Book> page = new PageImpl<>(List.of(exampleBook));
        when(bookRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(page);

        when(bookMapper.toDto(exampleBook)).thenReturn(new BookDto(
                exampleBook.getId(),
                exampleBook.getTitle(),
                exampleBook.getAuthor(),
                exampleBook.getIsbn(),
                exampleBook.getPrice(),
                exampleBook.getDescription(),
                exampleBook.getCoverImage(),
                null
        ));

        BookSearchParametersDto params = new BookSearchParametersDto("a", "b", "c");
        Pageable pageable = PageRequest.of(0, 10);

        Page<BookDto> result = bookService.search(params, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
        verify(bookMapper).toDto(exampleBook);
    }
}
