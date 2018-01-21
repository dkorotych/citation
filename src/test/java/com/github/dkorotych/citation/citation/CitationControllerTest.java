package com.github.dkorotych.citation.citation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dkorotych.citation.domain.Author;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CitationControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void getAllCitations() throws Exception {
        final MvcResult result = mockMvc.perform(
                get("/citation").
                        contentType(APPLICATION_JSON_UTF8)).
                andReturn();

        mockMvc.perform(asyncDispatch(result)).
//                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_UTF8)).
                andExpect(content().encoding(StandardCharsets.UTF_8.name())).
                andExpect(jsonPath("$", hasSize(36165))).
                andExpect(jsonPath("$[0].id").doesNotExist()).
                andExpect(jsonPath("$[0].author.id").doesNotExist()).
                andExpect(jsonPath("$[0].author").exists()).
                andExpect(jsonPath("$[0].author.name").exists()).
                andExpect(jsonPath("$[0].text").exists());
    }

    @Test
    public void getAny() throws Exception {
        mockMvc.perform(
                get("/citation/search/any").
                        contentType(APPLICATION_JSON_UTF8)).
//                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_UTF8)).
                andExpect(content().encoding(StandardCharsets.UTF_8.name())).
                andExpect(jsonPath("$.author.name").exists()).
                andExpect(jsonPath("$.text").exists());
    }

    @Test
    public void getObamaCitations() throws Exception {
        final String author = "Barack Obama";
        final MvcResult result = mockMvc.perform(
                post("/citation/search/all").
                        contentType(APPLICATION_JSON_UTF8).
                        content(new ObjectMapper().writeValueAsString(new Author(author)))).
                andReturn();

        final int count = 25;
        mockMvc.perform(asyncDispatch(result)).
//                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_UTF8)).
                andExpect(content().encoding(StandardCharsets.UTF_8.name())).
                andExpect(jsonPath("$", hasSize(count))).
                andExpect(jsonPath("$[*].author.name", equalTo(Collections.nCopies(count, author)))).
                andExpect(jsonPath("$[*].text").exists());
    }
}