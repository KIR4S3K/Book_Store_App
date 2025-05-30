package com.book.store.app.mapper;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.BookDtoWithoutCategoryIds;
import com.book.store.app.dto.CreateBookRequestDto;
import com.book.store.app.entity.Book;
import com.book.store.app.entity.Category;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    // Mapowanie encji Book na DTO, pomijamy automatyczne wypełnienie categoryIds
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    List<BookDto> toDtoList(List<Book> books);

    // Mapowanie DTO na encję Book, pomijamy kategorie (obsłużymy w serwisie)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Book toEntity(CreateBookRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateEntityFromDto(CreateBookRequestDto dto, @MappingTarget Book book);

    // Mapowanie dla endpointu GET /categories/{id}/books
    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    // Po mapowaniu pełnego BookDto dodajemy ręcznie set ID kategorii
    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book.getCategories() != null) {
            Set<Long> ids = book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            bookDto.setCategoryIds(ids);
        }
    }
}
