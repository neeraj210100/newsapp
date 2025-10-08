package org.demo.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.models.dto.NewsDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;


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

    @Value("${web.scraper.ignore-ssl:true}")
    private boolean ignoreSsl;

    static {
        // Configure SSL to ignore certificate validation issues
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            log.warn("Failed to configure SSL trust settings: {}", e.getMessage());
        }
    }

    /**
     * Fetches full article content from a given URL
     */
    public NewsDTO fetchArticleContent(NewsDTO newsDTO) {
        String url = newsDTO.getSourceUrl();
        if (!enabled || url == null || url.isEmpty()) {
            log.debug("Web scraping disabled or invalid URL");
            return newsDTO;
        }

        int maxRetries = 2;
        int retryCount = 0;
        
        while (retryCount <= maxRetries) {
            try {
                log.debug("Fetching content from URL: {} (attempt {})", url, retryCount + 1);
                Document doc = Jsoup
                        .connect(url)
                        .userAgent(userAgent)
                        .header("Accept-Language", "*")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Connection", "keep-alive")
                        .header("Upgrade-Insecure-Requests", "1")
                        .timeout(timeout)
                        .followRedirects(true)
                        .maxBodySize(0) // No limit on body size
                        .get();

                // Extract article content
                String content = extractArticleContent(doc);
                if (content != null && !content.trim().isEmpty()) {
                    newsDTO.setContent(content);
                    log.debug("Successfully extracted content for article: {}", newsDTO.getTitle());
                }

                // Extract article image
                String imageUrl = extractArticleImage(doc, url);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    newsDTO.setImageUrl(imageUrl);
                    log.debug("Successfully extracted image URL for article: {}", newsDTO.getTitle());
                }

                // If we get here, the request was successful
                break;

            } catch (Exception e) {
                retryCount++;
                if (retryCount > maxRetries) {
                    log.error("Error fetching content from URL {} after {} attempts: {}", url, maxRetries + 1, e.getMessage());
                } else {
                    log.warn("Error fetching content from URL {} (attempt {}): {}. Retrying...", url, retryCount, e.getMessage());
                    try {
                        Thread.sleep(1000 * retryCount); // Wait before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        return newsDTO;
    }

    /**
     * Extracts the main article content from the document
     */
    private String extractArticleContent(Document doc) {
        // Common selectors for article content in order of preference
        String[] contentSelectors = {
            "article .content",
            "article .article-content", 
            "article .post-content",
            "article .entry-content",
            "article .article-body",
            "article .story-body",
            "article .article-text",
            "article p",
            ".content",
            ".article-content",
            ".post-content", 
            ".entry-content",
            ".article-body",
            ".story-body",
            ".article-text"
        };

        for (String selector : contentSelectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                StringBuilder content = new StringBuilder();
                for (Element element : elements) {
                    // Remove script and style elements
                    element.select("script, style, .advertisement, .ads, .social-share").remove();
                    
                    String text = element.text().trim();
                    if (!text.isEmpty()) {
                        content.append(text).append("\n\n");
                    }
                }
                
                String extractedContent = content.toString().trim();
                if (extractedContent.length() > 100) { // Ensure we have substantial content
                    return extractedContent;
                }
            }
        }

        // Fallback: extract all paragraph text from the document
        Elements paragraphs = doc.select("p");
        if (!paragraphs.isEmpty()) {
            StringBuilder content = new StringBuilder();
            for (Element p : paragraphs) {
                String text = p.text().trim();
                if (text.length() > 20) { // Only include substantial paragraphs
                    content.append(text).append("\n\n");
                }
            }
            return content.toString().trim();
        }

        return null;
    }

    /**
     * Extracts the main article image from the document
     */
    private String extractArticleImage(Document doc, String baseUrl) {
        // Common selectors for article images in order of preference
        String[] imageSelectors = {
            "article img",
            ".article-image img",
            ".post-image img", 
            ".entry-image img",
            ".featured-image img",
            ".hero-image img",
            ".main-image img",
            ".article-header img",
            ".post-header img"
        };

        for (String selector : imageSelectors) {
            Elements images = doc.select(selector);
            for (Element img : images) {
                String src = img.attr("src");
                if (src != null && !src.isEmpty()) {
                    // Convert relative URLs to absolute URLs
                    String absoluteUrl = convertToAbsoluteUrl(src, baseUrl);
                    if (isValidImageUrl(absoluteUrl)) {
                        return absoluteUrl;
                    }
                }
            }
        }

        // Fallback: look for any image with reasonable dimensions
        Elements allImages = doc.select("img");
        for (Element img : allImages) {
            String src = img.attr("src");
            String width = img.attr("width");
            String height = img.attr("height");
            
            if (src != null && !src.isEmpty()) {
                // Prefer images with reasonable dimensions
                if (isValidImageDimensions(width, height)) {
                    String absoluteUrl = convertToAbsoluteUrl(src, baseUrl);
                    if (isValidImageUrl(absoluteUrl)) {
                        return absoluteUrl;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Converts relative URLs to absolute URLs
     */
    private String convertToAbsoluteUrl(String url, String baseUrl) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        
        if (url.startsWith("//")) {
            return "https:" + url;
        }
        
        if (url.startsWith("/")) {
            try {
                java.net.URL base = new java.net.URL(baseUrl);
                return base.getProtocol() + "://" + base.getHost() + url;
            } catch (Exception e) {
                log.warn("Error converting relative URL to absolute: {}", e.getMessage());
            }
        }
        
        return url;
    }

    /**
     * Validates if the URL points to a valid image
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        // Check for common image extensions
        String lowerUrl = url.toLowerCase();
        return lowerUrl.contains(".jpg") || lowerUrl.contains(".jpeg") || 
               lowerUrl.contains(".png") || lowerUrl.contains(".gif") || 
               lowerUrl.contains(".webp") || lowerUrl.contains(".svg");
    }

    /**
     * Validates if image dimensions are reasonable for article images
     */
    private boolean isValidImageDimensions(String width, String height) {
        try {
            if (width != null && !width.isEmpty() && height != null && !height.isEmpty()) {
                int w = Integer.parseInt(width);
                int h = Integer.parseInt(height);
                // Prefer images that are at least 200x200 pixels
                return w >= 200 && h >= 200;
            }
        } catch (NumberFormatException e) {
            // If dimensions can't be parsed, consider it valid
        }
        return true; // Consider valid if dimensions can't be determined
    }


}