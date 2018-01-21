package com.github.dkorotych.citation.citation;

import com.github.dkorotych.citation.author.AuthorEntity;
import com.github.dkorotych.citation.author.AuthorEntityToAuthorConverter;
import com.github.dkorotych.citation.author.AuthorService;
import com.github.dkorotych.citation.domain.Citation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
class CitationEntityToCitationConverter implements Converter<CitationEntity, Citation> {

    private final AuthorEntityToAuthorConverter converter;
    private final AuthorService authorService;

    @Autowired
    CitationEntityToCitationConverter(AuthorEntityToAuthorConverter converter,
                                      AuthorService authorService) {
        this.converter = converter;
        this.authorService = authorService;
    }

    @Override
    public Citation convert(CitationEntity source) {
        if (source == null) {
            return null;
        }
        Citation target = new Citation();
        BeanUtils.copyProperties(source, target, "authorId");
        AuthorEntity entity = authorService.findById(source.getAuthorId());
        target.setAuthor(converter.convert(entity));
        return target;
    }
}
