package com.github.dkorotych.citation.author;

import com.github.dkorotych.citation.AbstractConverterTest;
import com.github.dkorotych.citation.domain.Author;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class AuthorToAuthorEntityConverterTest extends AbstractConverterTest<Author, AuthorEntity> {

    @Autowired
    private AuthorToAuthorEntityConverter converter;

    @Test
    public void convert() {
        String name = "Arnold Schwarzenegger";
        Author author = new Author(name);
        AuthorEntity entity = new AuthorEntity();
        entity.setName(name);
        Assertions.assertThat(converter.convert(author)).
                isNotNull().
                isEqualToComparingOnlyGivenFields(entity, "name");
    }

    @Override
    protected Converter<Author, AuthorEntity> getConverter() {
        return converter;
    }
}