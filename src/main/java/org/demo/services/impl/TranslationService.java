package org.demo.services.impl;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TranslationService {

    @Value("${google.cloud.project.id}")
    private String projectId;

    public String translateText(String text, String targetLanguage) {
        try {
            Translate translate = TranslateOptions.newBuilder()
                    .setProjectId(projectId)
                    .build()
                    .getService();

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