package com.book.store.app.mapper;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.CreateBookRequestDto;
import com.book.store.app.entity.Book;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toDto(Book book);

    List<BookDto> toDtoList(List<Book> books);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Book toEntity(CreateBookRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(CreateBookRequestDto dto, @MappingTarget Book book);
}
