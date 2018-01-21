package com.github.dkorotych.citation;

import com.github.dkorotych.citation.citation.CitationService;
import com.github.dkorotych.citation.domain.Author;
import com.github.dkorotych.citation.domain.Citation;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@EnableRedisRepositories
public class StorageConfiguration {

    @EventListener(ApplicationPreparedEvent.class)
    public void initializeStorage(ApplicationPreparedEvent event) throws IOException {
        final ConfigurableApplicationContext context = event.getApplicationContext();
        CitationService citationService = context.getBean(CitationService.class);
        CSVParser parser = new CSVParserBuilder().
                withSeparator('\t').
                build();
        try (CSVReader reader = new CSVReaderBuilder(
                new BufferedReader(new InputStreamReader(
                        CitationApplication.class.getResourceAsStream("/quotes.txt"),
                        StandardCharsets.UTF_8))).
                withCSVParser(parser).
                build()) {
            List<Citation> citations = new ArrayList<>();
            AtomicReference<Author> previousAuthor = new AtomicReference<>();
            reader.forEach(strings -> {
                String author = strings[0];
                String citation = strings[1];

                final Boolean equal = Optional.ofNullable(previousAuthor.get()).
                        map(Author::getName).
                        map(name -> Objects.equals(name, author)).
                        orElse(false);

                if (!equal) {
                    previousAuthor.set(new Author(author));
                }
                Citation item = new Citation();
                item.setAuthor(previousAuthor.get());
                item.setText(citation);
                citations.add(item);
            });
            citationService.initialize(citations);
        }
    }
}
