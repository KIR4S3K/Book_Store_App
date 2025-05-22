package com.book.store.app.dto;

public record BookSearchParametersDto(
        String title,
        String author,
        String isbn
) {
}
