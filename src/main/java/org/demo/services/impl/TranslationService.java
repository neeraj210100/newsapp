package org.demo.services.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class TranslationService {

    @Value("${google.cloud.project.id}")
    private String projectId;

    private Translate getTranslateService() throws IOException {
        try {
            InputStream credentialsStream = new ClassPathResource("google-credentials.json").getInputStream();
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            
            return TranslateOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            log.error("Error loading Google credentials: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String translateText(String text, String targetLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        try {
            Translate translate = getTranslateService();

            Translation translation = translate.translate(
                    text,
                    Translate.TranslateOption.targetLanguage(targetLanguage)
            );

            log.debug("Successfully translated text to {}", targetLanguage);
            return translation.getTranslatedText();
        } catch (Exception e) {
            log.error("Error translating text to {}: {}", targetLanguage, e.getMessage(), e);
            throw new RuntimeException("Translation failed: " + e.getMessage(), e);
        }
    }
} 