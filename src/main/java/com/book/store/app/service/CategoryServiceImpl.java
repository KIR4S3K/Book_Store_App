package com.book.store.app.service;

import com.book.store.app.dto.BookDtoWithoutCategoryIds;
import com.book.store.app.dto.CategoryDto;
import com.book.store.app.entity.Book;
import com.book.store.app.entity.Category;
import com.book.store.app.mapper.BookMapper;
import com.book.store.app.mapper.CategoryMapper;
import com.book.store.app.repository.BookRepository;
import com.book.store.app.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    private final BookRepository bookRepo;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return categoryMapper.toDtoList(categoryRepo
                .findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category cat = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: "
                        + id));
        return categoryMapper.toDto(cat);
    }

    @Override
    @Transactional
    public CategoryDto save(CategoryDto dto) {
        Category entity = categoryMapper.toEntity(dto);
        return categoryMapper.toDto(categoryRepo.save(entity));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category existing = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: "
                        + id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return categoryMapper.toDto(categoryRepo.save(existing));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        categoryRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id) {
        List<Book> books = bookRepo.findAllByCategoryId(id);
        return books.stream()
                .map(bookMapper::toDtoWithoutCategories)
                .collect(Collectors.toList());
    }
}
