package com.github.dkorotych.citation.citation;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitationRepository extends KeyValueRepository<CitationEntity, Long> {
    CitationEntity findByIndexedText(String text);
}
