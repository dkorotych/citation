package com.github.dkorotych.citation.citation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dkorotych.citation.domain.Author;
import com.github.dkorotych.citation.domain.Citation;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
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
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(JUnitParamsRunner.class)
@SpringBootTest
public class CitationControllerTest {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

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
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_UTF8)).
                andExpect(content().encoding(StandardCharsets.UTF_8.name())).
                andExpect(jsonPath("$", hasSize(count))).
                andExpect(jsonPath("$[*].author.name", equalTo(Collections.nCopies(count, author)))).
                andExpect(jsonPath("$[*].text").exists());
    }

    @Test
    @Parameters
    public void findByText(String text, Citation[] expected) throws Exception {
        mockMvc.perform(
                post("/citation/search").
                        contentType(TEXT_PLAIN).
                        content(text)).
                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_UTF8)).
                andExpect(content().encoding(StandardCharsets.UTF_8.name())).
                andExpect(jsonPath("$", hasSize(expected.length))).
                andExpect(content().json(new ObjectMapper().writeValueAsString(expected), false));
    }

    private Object[][] parametersForFindByText() {
        return new Object[][]{
                {
                        "versac",
                        new Citation[]{
                                createCitation("Naomi Campbell", "After Versace was murdered, the first person to call me was Mandela.")
                        }
                },
                {
                        "Versace",
                        new Citation[]{
                                createCitation("Naomi Campbell", "After Versace was murdered, the first person to call me was Mandela.")
                        }
                },
                {
                        "mode?ling",
                        new Citation[]{
                                createCitation("Georgia May Jagger", "My dad was fine about me doing modelling at 16 because I always said school was important to me. I always chose my jobs carefully so I wouldn't have to take too much time off. It got harder toward the end with my A-levels; there were sleepless nights, and I was doing my homework on the plane coming home, but I pulled through."),
                                createCitation("Caitriona Balfe", "Modelling wasn't a passion of mine, so that made it get old kind of quickly. I was getting very frustrated."),
                                createCitation("Carmen Ejogo", "I was this very precocious kid with a big personality. When my mother saw that modelling was something I enjoyed, she didn't dissuade me."),
                        }
                },
                {
                        "mode*ling",
                        new Citation[]{
                                createCitation("Georgia May Jagger", "My dad was fine about me doing modelling at 16 because I always said school was important to me. I always chose my jobs carefully so I wouldn't have to take too much time off. It got harder toward the end with my A-levels; there were sleepless nights, and I was doing my homework on the plane coming home, but I pulled through."),
                                createCitation("Caitriona Balfe", "Modelling wasn't a passion of mine, so that made it get old kind of quickly. I was getting very frustrated."),
                                createCitation("Carmen Ejogo", "I was this very precocious kid with a big personality. When my mother saw that modelling was something I enjoyed, she didn't dissuade me."),
                                createCitation("Carla Hall", "I was in Paris, Milan and London from '89 until '91, and I did mostly runway modeling. I know there's so many people out there looking for pictures, but this was way before the age of the Internet, sorry!"),
                                createCitation("Chanel Iman", "I don't get facials. The last time I got a facial was when I first started modeling when I was 15 or 16. It made my face completely break out."),
                                createCitation("Chanel Iman", "I'm thinking of slowing down on modeling and branching out to other things. I want to pursue some new and old dreams and start making them happen."),
                                createCitation("Georgia Jagger", "I didn't want to miss out on my education to model. I can't do just modeling."),
                                createCitation("Georgia Jagger", "Modeling is always something I've really admired because I've seen my mum and sister do it."),
                                createCitation("Godfrey Gao", "After I grew some facial hair, I looked a bit older, and I guess that's what the modeling world wanted because I started booking more luxury brands."),
                                createCitation("Jacquelyn Jablonski", "Before I started modeling, I had never been out of the country, and now I feel like I'm out of the country at least a few times a month, if not once a week."),
                                createCitation("Jacquelyn Jablonski", "I also want to try acting - give it a shot - maybe take some lessons, I think that could be fun. I feel like that could even help me with modeling, because in a way you have to act."),
                                createCitation("Jacquelyn Jablonski", "Modeling was something I wanted to try from a really young age."),
                                createCitation("Jerry Hall", "I'd always enjoyed acting, but modeling was so time-consuming - and lucrative - that I didn't pursue it."),
                                createCitation("Joseph Gatt", "I did a lot of modeling in the U.K. A lot of it wasn't high fashion because I don't have the body or the face for high fashion modeling. I did a lot of sportswear, swimwear, and beachwear."),
                                createCitation("Kathy Ireland", "It's been an extraordinary journey. I have learned so much along the way. I entered the modeling industry as a business person already. I always knew I belonged on the other side of the camera."),
                                createCitation("Kathy Ireland", "Because modeling is lucrative, I'm able to save up and be more particular about the acting roles I take."),
                                createCitation("Kathy Ireland", "I entered the modeling industry as a business person already. I always knew I belonged on the other side of the camera."),
                                createCitation("Kevyn Aucoin", "The faces I see in the modeling industry can get dull."),
                                createCitation("Kirk Cameron", "The Hollywood lifestyle was just overwhelming. A party here, an interview there, magazine and modeling shoots daily, your face everywhere and girls throwing themselves at you. As great as it felt at the time, I still felt something missing, and that I needed to change."),
                                createCitation("Maud Adams", "Modeling is a great beginning, but it's also a kind of trap if you have any ambition or a mind that needs to be stimulated."),
                                createCitation("Max Irons", "With acting, I've got a character to inhabit. You've got to think about your intentions and your directions. In modeling, even though there's an act to it, a good model is a good model. For me it's uncomfortable territory. You start to feel quite insecure about yourself. There's nothing between you and the camera, and it's just you."),
                                createCitation("Naomi Campbell", "I've been offered jobs by companies that supported apartheid many times in the 25 years of my modeling career, but I have never taken one of them. I have to refuse that money, because I'm not going to work against my people. They've suffered enough."),
                                createCitation("Naomi Campbell", "When I started out modeling, there weren't casting directors and there weren't stylists, so you just dealt directly with the designer. We were all much closer back then."),
                                createCitation("Shannon Elizabeth", "When I was modeling, I worked out every day or other day."),
                                createCitation("Tunde Adebimpe", "When I'm in the mode of feeling positive about love, I don't really feel the need to mark it down in song. In fact, I know what that song would sound like, and I would not subject anybody to that."),
                                createCitation("James Fenton", "When we study Shakespeare on the page, for academic purposes, we may require all kinds of help. Generally, we read him in modern spelling and with modern punctuation, and with notes. But any poetry that is performed - from song lyric to tragic speech - must make its point, as it were, without reference back."),
                                createCitation("Mitt Romney", "And fifth, we will champion small businesses, America's engine of job growth. That means reducing taxes on business, not raising them. It means simplifying and modernizing the regulations that hurt small business the most. And it means that we must rein in the skyrocketing cost of healthcare by repealing and replacing Obamacare."),
                                createCitation("Feist", "Well, there's just some universal truths in a way that I've just observed to be true. You read Voltaire. You read modern literature. Anywhere you go, there's these observations about romantic love and what it does people, and these rotten feelings that rarely are people meaning to do that to each other."),
                                createCitation("Jacquelyn Jablonski", "I remember hearing other models talk about going to castings for Givenchy, and I was like, 'What are they saying?' And then I realized and was like, 'Oh, the Give-in-chee one.' I had been calling it Give-in-chee the whole time. I was shocked."),
                                createCitation("Lane Garrison", "Bonnie and Clyde were almost like a modern-day Robin Hood, stealing 'the government's money.' I think that's a bit of why they were glorified."),
                                createCitation("Edward Bach", "The main reason for the failure of the modern medical science is that it is dealing with results and not causes. Nothing more than the patching up of those attacked and the burying of those who are slain, without a thought being given to the real strong hold."),
                                createCitation("Brian Acton", "Your insurance broker has your telephone number, but your insurance broker doesn't have your Facebook ID. I think they are very different modes of communication. Commingling them can come with risk and peril."),
                                createCitation("William Baldwin", "I could count my modeling jobs on my hands and toes. When I graduated from college, I moved to New York specifically to study acting, and I needed to pay the bills, and it's better to make a couple thousand dollars in one day than to wait tables six days a week.")
                        }
                }
        };
    }

    private Citation createCitation(String author, String text) {
        Citation citation = new Citation();
        citation.setAuthor(new Author(author));
        citation.setText(text);
        return citation;
    }
}