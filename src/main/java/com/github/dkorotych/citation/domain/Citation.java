package com.github.dkorotych.citation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class Citation {
    @NotEmpty
    private Author author;
    @NotBlank
    private String text;
}
