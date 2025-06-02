package com.book.store.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.book.store.app.dto.BookDtoWithoutCategoryIds;
import com.book.store.app.dto.CategoryDto;
import com.book.store.app.entity.Book;
import com.book.store.app.entity.Category;
import com.book.store.app.mapper.BookMapper;
import com.book.store.app.mapper.CategoryMapper;
import com.book.store.app.repository.BookRepository;
import com.book.store.app.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepo;

    @Mock
    private BookRepository bookRepo;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category exampleCategory;
    private CategoryDto exampleDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exampleCategory = new Category();
        exampleCategory.setId(1L);
        exampleCategory.setName("Sci-Fi");
        exampleCategory.setDescription("Science fiction");

        exampleDto = new CategoryDto();
        exampleDto.setName("New Name");
        exampleDto.setDescription("New Description");
    }

    @Test
    @DisplayName("findAll returns a list of all categories")
    void findAll_returnsList() {
        when(categoryRepo.findAll()).thenReturn(List.of(exampleCategory));
        when(categoryMapper.toDtoList(List.of(exampleCategory)))
                .thenReturn(List.of(new CategoryDto(1L, "Sci-Fi", "Science fiction")));

        List<CategoryDto> result = categoryService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Sci-Fi");
        verify(categoryRepo).findAll();
    }

    @Test
    @DisplayName("getById returns CategoryDto when entity exists")
    void getById_found() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(exampleCategory));
        when(categoryMapper.toDto(exampleCategory))
                .thenReturn(new CategoryDto(1L, "Sci-Fi", "Science fiction"));

        CategoryDto result = categoryService.getById(1L);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Sci-Fi");
        verify(categoryRepo).findById(1L);
    }

    @Test
    @DisplayName("getById throws EntityNotFoundException when entity is missing")
    void getById_notFound() {
        when(categoryRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(2L));
    }

    @Test
    @DisplayName("save successfully saves a new category")
    void save_creates() {
        Category toSave = new Category();
        when(categoryMapper.toEntity(exampleDto)).thenReturn(toSave);
        when(categoryRepo.save(toSave)).thenReturn(exampleCategory);
        when(categoryMapper.toDto(exampleCategory))
                .thenReturn(new CategoryDto(1L, "Sci-Fi", "Science fiction"));

        CategoryDto result = categoryService.save(exampleDto);
        assertThat(result.getId()).isEqualTo(1L);
        verify(categoryMapper).toEntity(exampleDto);
        verify(categoryRepo).save(toSave);
    }

    @Test
    @DisplayName("update correctly modifies a category")
    void update_existing() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(exampleCategory));

        when(categoryRepo.save(exampleCategory)).thenReturn(exampleCategory);
        when(categoryMapper.toDto(exampleCategory))
                .thenReturn(new CategoryDto(1L, "New Name", "New Description"));

        CategoryDto result = categoryService.update(1L, exampleDto);
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Description");
        verify(categoryRepo).findById(1L);
        verify(categoryRepo).save(exampleCategory);
    }

    @Test
    @DisplayName("update throws EntityNotFoundException when entity is missing")
    void update_notFound() {
        when(categoryRepo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.update(5L, exampleDto));
    }

    @Test
    @DisplayName("deleteById removes the category")
    void deleteById_existing() {
        doNothing().when(categoryRepo).deleteById(1L);
        categoryService.deleteById(1L);
        verify(categoryRepo).deleteById(1L);
    }

    @Test
    @DisplayName("getBooksByCategoryId returns list of BookDtoWithoutCategoryIds")
    void getBooksByCategory_existing() {
        Book book1 = new Book();
        book1.setId(10L);
        book1.setTitle("Book1");
        book1.setAuthor("Author1");
        book1.setIsbn("ISBN-01");
        book1.setPrice(new BigDecimal("10"));
        when(bookRepo.findAllByCategoryId(1L)).thenReturn(List.of(book1));
        when(bookMapper.toDtoWithoutCategories(book1))
                .thenReturn(new BookDtoWithoutCategoryIds(10L, "Book1",
                        "Author1", "ISBN-01", new BigDecimal("10"),
                        null, null));

        var result = categoryService.getBooksByCategoryId(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10L);
        verify(bookRepo).findAllByCategoryId(1L);
    }
}
