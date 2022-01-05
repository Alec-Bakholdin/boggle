package com.alecbakholdin.boggle.data;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Component
public class WordValidation {
    private static final HashSet<String> validWords = new HashSet<>();

    public WordValidation(ResourcePatternResolver resourcePatternResolver) throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("classpath:words/*");
        for(Resource resource : resources) {
            log.info("Loading file " + resource.getFilename());
            Set<String> wordsInResource = getWordsInResource(resource);
            validWords.addAll(wordsInResource);
            log.info(String.format("Loaded %d words from %s", wordsInResource.size(), resource.getFilename()));
        }
    }

    public static boolean isValidWord(String word) {
        return validWords.contains(word.toUpperCase(Locale.ROOT));
    }

    private Set<String> getWordsInResource(Resource resource) throws IOException {
        InputStreamReader resourceReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        String resourceContents = FileCopyUtils.copyToString(resourceReader);
        return Arrays.stream(resourceContents.split("\r?\n"))
                .map(word -> word.toUpperCase(Locale.ROOT))
                .collect(Collectors.toCollection(HashSet::new));
    }
}
