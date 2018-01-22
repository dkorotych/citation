package com.github.dkorotych.citation.author;

import com.github.dkorotych.citation.EntityNotFoundException;
import com.github.dkorotych.citation.domain.Author;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.StreamSupport;

@Service
@Validated
public class AuthorService {

    private final AuthorRepository repository;
    private final AuthorToAuthorEntityConverter toEntityConverter;
    private final AuthorEntityToAuthorConverter toAuthorConverter;

    @Autowired
    public AuthorService(AuthorRepository repository,
                         AuthorToAuthorEntityConverter toEntityConverter,
                         AuthorEntityToAuthorConverter toAuthorConverter) {
        this.repository = repository;
        this.toEntityConverter = toEntityConverter;
        this.toAuthorConverter = toAuthorConverter;
    }

    public Map<String, AuthorEntity> initialize(List<Author> authors) {
        Collection<AuthorEntity> entities = new ConcurrentLinkedQueue<>();
        authors.parallelStream().
                distinct().
                filter(author -> Objects.isNull(repository.findByName(author.getName()))).
                forEach(author -> {
                    AuthorEntity entity = new AuthorEntity();
                    final String name = author.getName();
                    entity.setName(name);
                    entities.add(entity);
                });
        repository.save(entities);
        Map<String, AuthorEntity> mapper = new ConcurrentHashMap<>(authors.size());
        StreamSupport.stream(repository.findAll().spliterator(), true).
                forEach(entity -> mapper.put(entity.getName(), entity));
        return mapper;
    }

    public AuthorEntity convert(@Nullable Author author) {
        return toEntityConverter.convert(author);
    }

    public Author findByName(@NotBlank String name) {
        return Optional.ofNullable(repository.findByName(name)).
                map(toAuthorConverter::convert).
                orElseThrow(() -> EntityNotFoundException.notFoundByName(new Author(name)));
    }

    public void save(Collection<AuthorEntity> author) {
        repository.save(author);
    }

    public AuthorEntity findById(Long id) {
        return repository.findOne(id);
    }

    public Iterable<AuthorEntity> findAll() {
        return repository.findAll();
    }
}
