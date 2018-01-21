package com.github.dkorotych.citation.author;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AuthorRepository extends KeyValueRepository<AuthorEntity, Long> {
    AuthorEntity findByName(String name);
}
