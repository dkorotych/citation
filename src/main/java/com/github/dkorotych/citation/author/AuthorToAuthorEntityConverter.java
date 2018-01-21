package com.github.dkorotych.citation.author;

import com.github.dkorotych.citation.domain.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class AuthorToAuthorEntityConverter implements Converter<Author, AuthorEntity> {
    private final AuthorRepository repository;

    @Autowired
    AuthorToAuthorEntityConverter(AuthorRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuthorEntity convert(Author author) {
        if (author == null) {
            return null;
        }
        if (StringUtils.hasText(author.getName())) {
            return repository.findByName(author.getName());
        } else {
            return new AuthorEntity();
        }
    }
}
