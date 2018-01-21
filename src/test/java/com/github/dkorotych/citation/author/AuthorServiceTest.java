package com.github.dkorotych.citation.author;

import com.github.dkorotych.citation.EntityNotFoundException;
import com.github.dkorotych.citation.domain.Author;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthorServiceTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private AuthorService service;

    @Test
    public void findByName() {
        String name = "Zsa Zsa Gabor";
        Assertions.assertThat(service.findByName(name)).
                isEqualTo(buildAuthor(name));

        name = "Arnold Schwarzenegger";
        Assertions.assertThat(service.findByName(name)).
                isEqualTo(buildAuthor(name));

        expectedException.expect(EntityNotFoundException.class);
        expectedException.expectMessage("Author not found by name: test");
        service.findByName("test");
    }

    private Author buildAuthor(String name) {
        return new Author(name);
    }
}