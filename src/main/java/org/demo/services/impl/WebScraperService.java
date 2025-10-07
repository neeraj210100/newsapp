package org.demo.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebScraperService {

    private final WebClient.Builder webClientBuilder;

    @Value("${web.scraper.timeout:10000}")
    private int timeout;

    @Value("${web.scraper.user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36}")
    private String userAgent;

    @Value("${web.scraper.enabled:true}")
    private boolean enabled;

    /**
     * Fetches full article content from a given URL
     */
    public String fetchArticleContent(String url) {
        if (!enabled || url == null || url.isEmpty()) {
            log.debug("Web scraping disabled or invalid URL");
            return null;
        }

        try {
            log.debug("Fetching content from URL: {}", url);
            
            String htmlContent = String.valueOf(webClientBuilder.build()
                    .get()
                    .uri(url)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeout)));

            if (htmlContent != null && !htmlContent.isEmpty()) {
                String content = extractContentFromHtml(htmlContent);
                
                if (content != null && !content.isEmpty()) {
                    log.debug("Successfully extracted {} characters of content from {}", content.length(), url);
                    return content;
                } else {
                    log.warn("No content found at URL: {}", url);
                    return null;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error fetching content from URL {}: {}", url, e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the main article content from HTML using regex patterns
     */
    private String extractContentFromHtml(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }

        StringBuilder content = new StringBuilder();
        
        // Extract text from <p> tags
        Pattern pPattern = Pattern.compile("<p[^>]*>(.*?)</p>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher pMatcher = pPattern.matcher(html);
        
        while (pMatcher.find()) {
            String paragraph = pMatcher.group(1);
            // Remove HTML tags from the paragraph
            String cleanText = paragraph.replaceAll("<[^>]+>", "").trim();
            // Decode common HTML entities
            cleanText = decodeHtmlEntities(cleanText);
            
            if (cleanText.length() > 50) { // Filter out short paragraphs
                content.append(cleanText).append("\n\n");
            }
        }

        String result = content.toString().trim();
        return result.length() > 100 ? result : null;
    }

    /**
     * Decodes common HTML entities
     */
    private String decodeHtmlEntities(String text) {
        return text
                .replaceAll("&nbsp;", " ")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&quot;", "\"")
                .replaceAll("&#39;", "'")
                .replaceAll("&apos;", "'")
                .replaceAll("&mdash;", "—")
                .replaceAll("&ndash;", "–")
                .replaceAll("&rsquo;", "'")
                .replaceAll("&lsquo;", "'")
                .replaceAll("&rdquo;", "\"")
                .replaceAll("&ldquo;", "\"");
    }

    /**
     * Extracts image URLs from a given article URL
     */
    public List<String> fetchArticleImages(String url) {
        if (!enabled || url == null || url.isEmpty()) {
            log.debug("Web scraping disabled or invalid URL");
            return new ArrayList<>();
        }

        try {
            log.debug("Fetching images from URL: {}", url);
            
            String htmlContent = String.valueOf(webClientBuilder.build()
                    .get()
                    .uri(url)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeout)));


            List<String> imageUrls = new ArrayList<>();

            if (htmlContent != null && !htmlContent.isEmpty()) {
                // Extract Open Graph image (og:image)
                Pattern ogImagePattern = Pattern.compile("<meta[^>]+property=[\"']og:image[\"'][^>]+content=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
                Matcher ogMatcher = ogImagePattern.matcher(htmlContent);
                if (ogMatcher.find()) {
                    String imgUrl = ogMatcher.group(1);
                    if (isValidImageUrl(imgUrl)) {
                        imageUrls.add(imgUrl);
                        log.debug("Found OG image: {}", imgUrl);
                    }
                }

                // Extract Twitter image
                if (imageUrls.isEmpty()) {
                    Pattern twitterImagePattern = Pattern.compile("<meta[^>]+name=[\"']twitter:image[\"'][^>]+content=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
                    Matcher twitterMatcher = twitterImagePattern.matcher(htmlContent);
                    if (twitterMatcher.find()) {
                        String imgUrl = twitterMatcher.group(1);
                        if (isValidImageUrl(imgUrl)) {
                            imageUrls.add(imgUrl);
                            log.debug("Found Twitter image: {}", imgUrl);
                        }
                    }
                }

                // Extract img src tags
                if (imageUrls.isEmpty()) {
                    Pattern imgPattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
                    Matcher imgMatcher = imgPattern.matcher(htmlContent);
                    
                    int count = 0;
                    while (imgMatcher.find() && count < 5) {
                        String imgUrl = imgMatcher.group(1);
                        if (isValidImageUrl(imgUrl)) {
                            imageUrls.add(imgUrl);
                            log.debug("Found image: {}", imgUrl);
                            count++;
                        }
                    }
                }
            }

            log.debug("Found {} images from {}", imageUrls.size(), url);
            return imageUrls;
        } catch (Exception e) {
            log.error("Error fetching images from URL {}: {}", url, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Validates if a URL is a valid image URL
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        String lowerUrl = url.toLowerCase();
        return (lowerUrl.endsWith(".jpg") || 
                lowerUrl.endsWith(".jpeg") || 
                lowerUrl.endsWith(".png") || 
                lowerUrl.endsWith(".gif") || 
                lowerUrl.endsWith(".webp") ||
                lowerUrl.contains(".jpg?") ||
                lowerUrl.contains(".jpeg?") ||
                lowerUrl.contains(".png?") ||
                lowerUrl.contains(".webp?")) &&
               (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * Fetches the best available image from a URL
     */
    public String fetchBestImage(String url) {
        List<String> images = fetchArticleImages(url);
        return images.isEmpty() ? null : images.get(0);
    }
}