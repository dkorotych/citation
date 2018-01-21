package com.github.dkorotych.citation;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CitationApplicationTests {

    private static final String AUTHORS = "authors";
    private static final String CITATIONS = "citations";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void initialized() {
        Assertions.assertThat(redisTemplate.hasKey(AUTHORS)).isTrue();
        Assertions.assertThat(redisTemplate.hasKey(CITATIONS)).isTrue();
        Assertions.assertThat(redisTemplate.hasKey("demo")).isFalse();
        final SetOperations<String, String> set = redisTemplate.opsForSet();
        Assertions.assertThat(set.size(AUTHORS)).isEqualTo(2297);
        Assertions.assertThat(set.size(CITATIONS)).isEqualTo(36165);
    }
}
