package com.book.store.app.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.BookSearchParametersDto;
import com.book.store.app.dto.CreateBookRequestDto;
import com.book.store.app.security.JwtAuthenticationFilter;
import com.book.store.app.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = BookController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes
                = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/books returns a page of books")
    void getAllBooks() throws Exception {
        BookDto dto = new BookDto(
                1L,
                "Title",
                "Author",
                "ISBN",
                new BigDecimal("19.99"),
                "Desc",
                "cover.jpg",
                Set.of()
        );
        Page<BookDto> page = new PageImpl<>(List.of(dto));

        Mockito.when(bookService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "title,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Title")));
    }

    @Test
    @DisplayName("GET /api/books/{id} returns BookDto when it exists")
    void getBookById_exists() throws Exception {
        BookDto dto = new BookDto(
                2L,
                "Book2",
                "Author2",
                "ISBN2",
                new BigDecimal("29.99"),
                "Desc2",
                "cover2.jpg",
                Set.of()
        );
        Mockito.when(bookService.findById(2L)).thenReturn(dto);

        mockMvc.perform(get("/api/books/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("Book2")));
    }

    @Test
    @DisplayName("POST /api/books creates a new book and returns 201")
    void createBook() throws Exception {
        CreateBookRequestDto req = new CreateBookRequestDto(
                "New", "AuthorN", "ISBNN", new BigDecimal("39.99"),
                "DescN", "coverN.jpg", Set.of()
        );
        BookDto resp = new BookDto(
                3L,
                "New",
                "AuthorN",
                "ISBNN",
                new BigDecimal("39.99"),
                "DescN",
                "coverN.jpg",
                Set.of()
        );

        Mockito.when(bookService.save(any(CreateBookRequestDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("New")));
    }

    @Test
    @DisplayName("PUT /api/books/{id} updates a book")
    void updateBook() throws Exception {
        CreateBookRequestDto req = new CreateBookRequestDto(
                "Updated", "AuthorU", "ISBNU", new BigDecimal("49.99"),
                "DescU", "coverU.jpg", Set.of()
        );
        BookDto resp = new BookDto(
                4L,
                "Updated",
                "AuthorU",
                "ISBNU",
                new BigDecimal("49.99"),
                "DescU",
                "coverU.jpg",
                Set.of()
        );

        Mockito.when(bookService.update(eq(4L), any(CreateBookRequestDto.class))).thenReturn(resp);

        mockMvc.perform(put("/api/books/{id}", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.title", is("Updated")));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} returns 204 NO_CONTENT")
    void deleteBook() throws Exception {
        Mockito.doNothing().when(bookService).delete(5L);

        mockMvc.perform(delete("/api/books/{id}", 5L))
                .andExpect(status().isNoContent());
        Mockito.verify(bookService).delete(5L);
    }

    @Test
    @DisplayName("GET /api/books/search returns search results")
    void searchBooks() throws Exception {
        BookDto dto = new BookDto(
                6L,
                "SearchResult",
                "AuthorS",
                "ISBNS",
                new BigDecimal("9.99"),
                "DescS",
                "coverS.jpg",
                Set.of()
        );
        Page<BookDto> page = new PageImpl<>(List.of(dto));

        Mockito.when(bookService.search(any(BookSearchParametersDto.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/books/search")
                        .param("title", "SearchResult")
                        .param("author", "AuthorS")
                        .param("isbn", "ISBNS")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(6)))
                .andExpect(jsonPath("$.content[0].title", is("SearchResult")));
    }
}
