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

import com.book.store.app.dto.BookDtoWithoutCategoryIds;
import com.book.store.app.dto.CategoryDto;
import com.book.store.app.security.JwtAuthenticationFilter;
import com.book.store.app.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = CategoryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = @Filter(type
                = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/categories returns a list of categories")
    void getAllCategories() throws Exception {
        CategoryDto dto = new CategoryDto(1L, "Fantasy", "Fantasy books");
        Mockito.when(categoryService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Fantasy")));
    }

    @Test
    @DisplayName("GET /api/categories/{id} returns category when it exists")
    void getCategoryById_exists() throws Exception {
        CategoryDto dto = new CategoryDto(2L, "Horror", "Horror books");
        Mockito.when(categoryService.getById(2L)).thenReturn(dto);

        mockMvc.perform(get("/api/categories/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Horror")));
    }

    @Test
    @DisplayName("POST /api/categories creates a category and returns 201")
    void createCategory() throws Exception {
        CategoryDto req = new CategoryDto(null, "Biography", "Biography books");
        CategoryDto resp = new CategoryDto(3L, "Biography", "Biography books");

        Mockito.when(categoryService.save(any(CategoryDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Biography")));
    }

    @Test
    @DisplayName("PUT /api/categories/{id} updates a category")
    void updateCategory() throws Exception {
        CategoryDto req = new CategoryDto(null, "Updated", "Updated description");
        CategoryDto resp = new CategoryDto(4L, "Updated", "Updated description");

        Mockito.when(categoryService.update(eq(4L), any(CategoryDto.class))).thenReturn(resp);

        mockMvc.perform(put("/api/categories/{id}", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} returns 204 NO_CONTENT")
    void deleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteById(5L);

        mockMvc.perform(delete("/api/categories/{id}", 5L))
                .andExpect(status().isNoContent());
        Mockito.verify(categoryService).deleteById(5L);
    }

    @Test
    @DisplayName("GET /api/categories/{id}/books returns books for the given category")
    void getBooksByCategoryId() throws Exception {
        BookDtoWithoutCategoryIds book = new BookDtoWithoutCategoryIds(
                10L, "BookTitle", "BookAuthor", "ISBNK",
                new BigDecimal("15.99"), "DescK", "coverK.jpg"
        );
        Mockito.when(categoryService.getBooksByCategoryId(7L)).thenReturn(List.of(book));

        mockMvc.perform(get("/api/categories/{id}/books", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].title", is("BookTitle")));
    }
}
