package com.github.dkorotych.citation.author;

import com.github.dkorotych.citation.AbstractConverterTest;
import com.github.dkorotych.citation.domain.Author;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class AuthorEntityToAuthorConverterTest extends AbstractConverterTest<AuthorEntity, Author> {

    @Autowired
    private AuthorEntityToAuthorConverter converter;

    @Test
    public void convert() {
        String name = "Arnold Schwarzenegger";
        Author author = new Author(name);
        AuthorEntity entity = new AuthorEntity();
        entity.setName(name);
        entity.setId(RandomUtils.nextLong());
        Assertions.assertThat(converter.convert(entity)).
                isNotNull().
                isEqualTo(author);
    }

    @Override
    protected Converter<AuthorEntity, Author> getConverter() {
        return converter;
    }
}