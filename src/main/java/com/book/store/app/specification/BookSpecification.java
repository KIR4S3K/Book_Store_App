package com.book.store.app.specification;

import com.book.store.app.dto.BookSearchParametersDto;
import com.book.store.app.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withSearchParams(BookSearchParametersDto params) {
        Specification<Book> spec = Specification.where(
                (root, cq, cb) -> cb.isFalse(root.get("deleted"))
        );

        if (params.title() != null && !params.title().isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + params.title().toLowerCase() + "%")
            );
        }

        if (params.author() != null && !params.author().isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.like(cb.lower(root.get("author")), "%" + params.author().toLowerCase() + "%")
            );
        }

        if (params.isbn() != null && !params.isbn().isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.like(cb.lower(root.get("isbn")), "%" + params.isbn().toLowerCase() + "%")
            );
        }

        return spec;
    }
}
