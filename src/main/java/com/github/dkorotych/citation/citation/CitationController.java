package com.github.dkorotych.citation.citation;

import com.github.dkorotych.citation.author.AuthorService;
import com.github.dkorotych.citation.domain.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(
        value = "/citation",
        produces = APPLICATION_JSON_UTF8_VALUE,
        consumes = APPLICATION_JSON_UTF8_VALUE)
@Validated
public class CitationController {
    private final CitationService citationService;
    private final AuthorService authorService;

    @Autowired
    public CitationController(CitationService citationService, AuthorService authorService) {
        this.citationService = citationService;
        this.authorService = authorService;
    }

    @RequestMapping(method = GET)
    public Callable<ResponseEntity> getAllCitations() {
        return () -> ResponseEntity.ok(citationService.findAll());
    }

    @RequestMapping(method = GET, path = "/search/any")
    public ResponseEntity getAny() {
        return ResponseEntity.ok(citationService.findAny());
    }

    @RequestMapping(method = POST, path = "/search/any")
    public ResponseEntity getAny(@Nullable @RequestBody Author author) {
        if (author == null || !StringUtils.hasText(author.getName())) {
            return ResponseEntity.ok(citationService.findAny());
        }
        return ResponseEntity.ok(citationService.findAny(authorService.findByName(author.getName())));
    }

    @RequestMapping(method = POST, path = "/search/all")
    public Callable<ResponseEntity> getAll(@Nullable @RequestBody Author author) {
        if (author == null || !StringUtils.hasText(author.getName())) {
            return () -> ResponseEntity.ok(citationService.findAll());
        }
        return () -> ResponseEntity.ok(citationService.findAllByAuthor(authorService.findByName(author.getName())));
    }
}
