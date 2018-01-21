package com.github.dkorotych.citation.author;

import com.github.dkorotych.citation.domain.Author;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AuthorEntityToAuthorConverter implements Converter<AuthorEntity, Author> {

    @Override
    public Author convert(AuthorEntity source) {
        if (source == null) {
            return null;
        }
        Author author = new Author();
        BeanUtils.copyProperties(source, author, "citations");
        return author;
    }
}
