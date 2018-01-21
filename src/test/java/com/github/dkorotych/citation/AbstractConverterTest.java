package com.github.dkorotych.citation;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class AbstractConverterTest<S, T> {

    @Test
    public void convertNull() {
        Assertions.assertThat(getConverter().convert(null)).isNull();
    }

    protected abstract Converter<S, T> getConverter();
}
