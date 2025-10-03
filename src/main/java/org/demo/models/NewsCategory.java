package org.demo.models;

public enum NewsCategory {
    TECHNOLOGY("Technology"),
    SPORTS("Sports"),
    POLITICS("Politics"),
    BUSINESS("Business"),
    HEALTH("Health"),
    ENTERTAINMENT("Entertainment"),
    SCIENCE("Science"),
    WORLD("World"),
    GENERAL("General");

    private final String displayName;

    NewsCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static NewsCategory fromString(String category) {
        if (category == null) {
            return GENERAL;
        }
        
        try {
            return NewsCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GENERAL;
        }
    }
} 