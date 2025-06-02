package com.book.store.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.book.store.app.entity.Category;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Should save and retrieve a Category entity")
    void saveAndFindById() {
        Category category = new Category();
        category.setName("Sci-Fi");
        category.setDescription("Science fiction books");

        Category saved = categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Sci-Fi");
        assertThat(found.get().getDescription()).isEqualTo("Science fiction books");
    }

    @Test
    @DisplayName("Deleting Category results in it being absent from the database")
    void deleteCategory() {
        Category category = new Category();
        category.setName("Horror");
        category.setDescription("Scary books");

        Category saved = categoryRepository.save(category);
        categoryRepository.deleteById(saved.getId());

        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}
