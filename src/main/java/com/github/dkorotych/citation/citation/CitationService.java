package com.github.dkorotych.citation.citation;

import com.github.dkorotych.citation.EntityNotFoundException;
import com.github.dkorotych.citation.author.AuthorEntity;
import com.github.dkorotych.citation.author.AuthorService;
import com.github.dkorotych.citation.domain.Author;
import com.github.dkorotych.citation.domain.Citation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Validated
public class CitationService {
    private static final String CITATIONS_TEXT = "citations-text";
    private final CitationRepository repository;
    private final AuthorService authorService;
    private final CitationEntityToCitationConverter toCitationConverter;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public CitationService(CitationRepository repository,
                           AuthorService authorService,
                           CitationEntityToCitationConverter toCitationConverter,
                           StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.authorService = authorService;
        this.toCitationConverter = toCitationConverter;
        this.redisTemplate = redisTemplate;
    }

    public void initialize(List<Citation> citations) {
        final Map<String, AuthorEntity> authors = authorService.initialize(
                citations.parallelStream().
                        map(Citation::getAuthor).
                        distinct().
                        collect(Collectors.toList()));
        Collection<CitationEntity> entities = new ConcurrentLinkedQueue<>();
        Map<Long, AuthorEntity> idsMapper = new ConcurrentHashMap<>(authors.size());
        citations.parallelStream().
                forEach(citation -> {
                    AuthorEntity authorEntity = authors.get(citation.getAuthor().getName());
                    String text = citation.getText();
                    CitationEntity entity = findByText(authorEntity, text);
                    if (entity == null) {
                        entity = new CitationEntity();
                        entity.setText(text);
                        entity.setIndexedText(toIndexedText(text));
                        final Long authorEntityId = authorEntity.getId();
                        entity.setAuthorId(authorEntityId);
                        entities.add(entity);
                        idsMapper.put(authorEntityId, authorEntity);
                    }
                });
        repository.save(entities).forEach(citationEntity -> {
            idsMapper.get(citationEntity.getAuthorId()).getCitations().add(citationEntity);
        });
        authorService.save(idsMapper.values());

        redisTemplate.delete(CITATIONS_TEXT);
        final String[] texts = citations.parallelStream().
                map(Citation::getText).
                map(CitationService::toIndexedText).
                toArray(String[]::new);
        redisTemplate.opsForSet().add(CITATIONS_TEXT, texts);
    }

    public List<Citation> findAll() {
        List<Citation> citations = new ArrayList<>(Long.valueOf(repository.count()).intValue());
        StreamSupport.stream(authorService.findAll().spliterator(), false).
                forEach(entity -> {
                    Author author = new Author();
                    author.setName(entity.getName());
                    entity.getCitations().forEach(citationEntity -> {
                        Citation citation = new Citation();
                        citation.setText(citationEntity.getText());
                        citation.setAuthor(author);
                        citations.add(citation);
                    });
                });
        return citations;
    }

    public List<Citation> findAllByAuthor(@NotNull Author author) {
        return Optional.ofNullable(authorService.convert(author)).
                map(AuthorEntity::getCitations).
                orElse(Collections.emptyList()).
                stream().
                map(toCitationConverter::convert).
                collect(Collectors.toList());
    }

    public Citation findAny() {
        final Long id = Long.valueOf(redisTemplate.opsForSet().randomMember("authors"));
        return Optional.ofNullable(authorService.findById(id)).
                map(AuthorEntity::getCitations).
                orElseThrow(() -> EntityNotFoundException.notFound(Author.class)).
                stream().
                findAny().
                map(toCitationConverter::convert).
                orElseThrow(() -> EntityNotFoundException.notFound(Citation.class));
    }

    public Citation findAny(@NotNull Author author) {
        return findAllByAuthor(author).stream().
                findAny().
                orElseThrow(() -> EntityNotFoundException.notFound(Citation.class));
    }

    public List<Citation> findByText(String text) {
        final ScanOptions options = ScanOptions.scanOptions().
                match(String.format("*%s*", toIndexedText(text))).
                build();
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(redisTemplate.opsForSet().
                        scan(CITATIONS_TEXT, options), 0), false).
                distinct().
                map(repository::findByIndexedText).
                map(toCitationConverter::convert).
                collect(Collectors.toList());
    }

    private static CitationEntity findByText(AuthorEntity entity, String text) {
        return Optional.ofNullable(entity.getCitations()).
                orElse(Collections.emptyList()).
                stream().
                filter(citationEntity -> text.contentEquals(citationEntity.getText())).
                findFirst().
                orElse(null);
    }

    private static String toIndexedText(String text) {
        return text.toLowerCase(Locale.ENGLISH);
    }
}
