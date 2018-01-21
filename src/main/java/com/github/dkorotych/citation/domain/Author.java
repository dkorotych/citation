package com.github.dkorotych.citation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@NoArgsConstructor
public class Author {
    @NotBlank
    private String name;

    public Author(String name) {
        this.name = name;
    }
}
