package com.github.dkorotych.citation;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;

@SpringBootApplication
@EnableAdminServer
public class CitationApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(CitationApplication.class, args);
    }
}
