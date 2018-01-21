package com.github.dkorotych.citation.citation;

import com.github.dkorotych.citation.author.AuthorService;
import com.github.dkorotych.citation.domain.Author;
import com.github.dkorotych.citation.domain.Citation;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CitationServiceTest {

    @Autowired
    private CitationService service;
    @Autowired
    private AuthorService authorService;

    @Test
    public void findAll() {
        Citation first = new Citation();
        first.setText("I don't remember anybody's name. How do you think the 'dahling' thing got started?");
        first.setAuthor(authorService.findByName("Zsa Zsa Gabor"));
        Citation second = new Citation();
        second.setText("People love gospel music. It's calming. It's soothing. It gets right to the point of whatever you're dealing with.");
        second.setAuthor(authorService.findByName("Yolanda Adams"));

        Assertions.assertThat(service.findAll()).
                hasSize(36165).
                extracting("text", "author").
                contains(
                        new Tuple(first.getText(), first.getAuthor()),
                        new Tuple(second.getText(), second.getAuthor()));
    }

    @Test
    public void findAllByAuthor() {
        Author author = authorService.findByName("Zsa Zsa Gabor");
        Assertions.assertThat(service.findAllByAuthor(author)).
                hasSize(25);

        author = authorService.findByName("Wolfman Jack");
        Assertions.assertThat(service.findAllByAuthor(author)).
                hasSize(25);

        author = authorService.findByName("Socrates");
        Assertions.assertThat(service.findAllByAuthor(author)).
                hasSize(25);
    }
}