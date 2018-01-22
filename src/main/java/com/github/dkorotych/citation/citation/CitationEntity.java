package com.github.dkorotych.citation.citation;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.validation.constraints.NotNull;

@RedisHash("citations")
@Data
@NoArgsConstructor
public class CitationEntity {
    @Id
    private Long id;
    @NotBlank
    private String text;
    @NotBlank
    @Indexed
    private String indexedText;
    @NotNull
    private Long authorId;
}
