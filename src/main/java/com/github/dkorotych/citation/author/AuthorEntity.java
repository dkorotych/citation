package com.github.dkorotych.citation.author;

import com.github.dkorotych.citation.citation.CitationEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@RedisHash("authors")
@Data
@NoArgsConstructor
public class AuthorEntity {
    @Id
    private Long id;
    @NotBlank
    @Indexed
    private String name;
    @NotEmpty
    @Reference
    private List<CitationEntity> citations = new ArrayList<>();
}
