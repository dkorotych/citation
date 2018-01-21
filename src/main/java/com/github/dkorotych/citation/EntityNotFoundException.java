package com.github.dkorotych.citation;

import com.github.dkorotych.citation.domain.Author;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class EntityNotFoundException extends RuntimeException {
    private EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException notFoundByName(@Nullable Author author) {
        final String entityType = Author.class.getSimpleName();
        final String name = Optional.ofNullable(author).map(Author::getName).orElse("null");
        String message = String.format("%s not found by name: %s", entityType, name);
        return new EntityNotFoundException(message);
    }

    public static EntityNotFoundException notFound(@NonNull Class<?> clazz) {
        final String entityType = clazz.getSimpleName();
        String message = String.format("%s not found", entityType);
        return new EntityNotFoundException(message);
    }
}
