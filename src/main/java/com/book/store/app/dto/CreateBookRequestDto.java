package com.book.store.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title can't be longer than 255 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author can't be longer than 255 characters")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Size(max = 20, message = "ISBN can't be longer than 20 characters")
    private String isbn;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or greater")
    private BigDecimal price;

    @Size(max = 2000, message = "Description can't be longer than 2000 characters")
    private String description;

    @Size(max = 255, message = "Cover image URL can't be longer than 255 characters")
    private String coverImage;

    private Set<Long> categoryIds;
}
