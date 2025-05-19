package com.book.store.app.mapper;

import com.book.store.app.dto.BookDto;
import com.book.store.app.dto.CreateBookRequestDto;
import com.book.store.app.entity.Book;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toDto(Book book);

    List<BookDto> toDtoList(List<Book> books);

    @Mapping(target = "id", ignore = true)
    Book toEntity(CreateBookRequestDto dto);
}
