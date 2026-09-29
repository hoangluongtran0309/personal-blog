package com.juliawalker.personalblog.article;

import java.nio.file.Path;
import java.time.Clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleConfiguration {

    @Bean
    public ArticleStore articleStore(@Value("${blog.articles.directory:./data/articles}") Path directory) {
        return new FileSystemArticleStore(directory);
    }

    // "publishedAt <= now" uses the server time zone; Clock is injected so tests can fix the time
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
