# NewsGram - Instagram-Style News App

A modern, Instagram-inspired news application built with React and Material-UI.

## Features

### 🎨 Instagram-Style Design
- **Card-based Layout**: News articles displayed as Instagram-style posts
- **Interactive Elements**: Like, save, share, and comment buttons
- **Responsive Design**: Optimized for mobile and desktop viewing
- **Modern UI**: Clean, minimalist interface with smooth animations

### 🌙 Theme Support
- **Dark/Light Mode**: Toggle between themes with persistent storage
- **Custom Color Schemes**: Optimized colors for both light and dark modes
- **Smooth Transitions**: Seamless theme switching experience

### 🌍 Multi-Language Support
- **10 Languages**: English, Spanish, French, German, Italian, Portuguese, Russian, Japanese, Korean, Chinese
- **Flag Icons**: Visual language selection with country flags
- **Real-time Translation**: News content translated to selected language

### 📂 Category Filtering
- **Dynamic Categories**: Filter news by various categories
- **Quick Access**: Category filter in the navigation bar
- **URL Integration**: Category selection reflected in URL parameters

### 🔍 Advanced Search
- **Keyword Search**: Search through news articles by keywords
- **Instant Results**: Real-time search with loading states
- **Instagram-style Results**: Search results displayed as Instagram posts

## Components

### InstagramPost
- **Like System**: Interactive like button with counter
- **Save Feature**: Bookmark articles for later reading
- **Share Functionality**: Native sharing or clipboard copy
- **Author Avatars**: Colorful avatars with initials
- **Category Tags**: Hashtag-style category display
- **Responsive Images**: Optimized image display with hover effects

### Navbar
- **Theme Toggle**: Dark/light mode switcher
- **Language Selector**: Dropdown with flag icons
- **Category Filter**: Quick category selection
- **Navigation**: Clean navigation between Feed and Search

### NewsList
- **Infinite Scroll**: Instagram-style vertical feed
- **Loading States**: Skeleton loaders for better UX
- **Error Handling**: User-friendly error messages
- **Floating Action Button**: Quick refresh functionality

## Technology Stack

- **React 18**: Modern React with hooks
- **Material-UI 5**: Component library with theming
- **React Router**: Client-side routing
- **Axios**: HTTP client for API calls
- **Moment.js**: Date formatting and manipulation

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server:
   ```bash
   npm start
   ```

3. Open [http://localhost:3000](http://localhost:3000) to view the app

## API Integration

The app connects to a Spring Boot backend with the following endpoints:
- `/api/news/dailyBulletin` - Get daily news
- `/api/news/search` - Search news by keyword
- `/api/news/external` - Fetch external news
- `/api/news/viewCategories` - Get available categories
- `/api/news/deleteNews/{id}` - Delete specific news
- `/api/news/deleteAll` - Delete all news

## Features in Detail

### Instagram-Style Posts
Each news article is displayed as an Instagram post with:
- Author avatar with colored background
- Post timestamp (relative time)
- Full-width image with hover effects
- Like, comment, share, and save buttons
- Category hashtags
- "Read more" link to original article

### Theme System
- **Light Mode**: Clean white background with dark text
- **Dark Mode**: Dark background with light text
- **Persistent Storage**: Theme preference saved in localStorage
- **Smooth Transitions**: Animated theme switching

### Language Support
- **Translation API**: Integrates with Google Translate
- **Language Persistence**: Selected language maintained across sessions
- **Visual Indicators**: Flag icons for easy language identification

### Category Filtering
- **Dynamic Loading**: Categories loaded from backend
- **Quick Filter**: One-click category filtering
- **URL Integration**: Category selection reflected in URL
- **Clear Filter**: Easy way to return to all categories

## Responsive Design

The app is fully responsive and optimized for:
- **Mobile**: Touch-friendly interface with appropriate spacing
- **Tablet**: Optimized layout for medium screens
- **Desktop**: Full-featured experience with hover effects

## Performance Optimizations

- **Lazy Loading**: Images loaded on demand
- **Skeleton Loaders**: Better perceived performance
- **Efficient Re-renders**: Optimized React components
- **Caching**: API responses cached for better performance

## Future Enhancements

- **Infinite Scroll**: Load more posts as user scrolls
- **Push Notifications**: Real-time news updates
- **Offline Support**: PWA capabilities
- **Social Features**: User accounts and following system
- **Advanced Filters**: Date range, source filtering
- **Bookmark Collections**: Organize saved articles
