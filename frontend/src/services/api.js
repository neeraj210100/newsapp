import axios from 'axios';

// Create axios instance with base configuration
// In production, use REACT_APP_API_URL if set, otherwise use relative URLs
// In development, use localhost:8080
const getBaseURL = () => {
  if (process.env.REACT_APP_API_URL) {
    return process.env.REACT_APP_API_URL;
  }
  if (process.env.NODE_ENV === 'production') {
    // In production, use relative URLs if frontend is served from same domain
    return '';
  }
  return 'http://localhost:8080';
};

const api = axios.create({
  baseURL: getBaseURL(),
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to: ${config.url}`);
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('Response error:', error);
    
    // Handle different error scenarios
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      switch (status) {
        case 404:
          throw new Error('Resource not found');
        case 500:
          throw new Error('Internal server error. Please try again later.');
        default:
          throw new Error(data?.message || `Server error: ${status}`);
      }
    } else if (error.request) {
      // Network error
      throw new Error('Network error. Please check your connection.');
    } else {
      // Other error
      throw new Error('An unexpected error occurred.');
    }
  }
);

// News API service methods
export const newsApi = {
  // Get daily news bulletin
  getDailyNews: (targetLanguage = 'en') => 
    api.get(`/api/news/dailyBulletin?targetLanguage=${targetLanguage}`),

  // Search news by keyword
  searchNews: (keyword) => 
    api.get(`/api/news/search?keyword=${encodeURIComponent(keyword)}`),

  // Fetch external news
  fetchExternalNews: (query, targetLanguage = 'en') => 
    api.get(`/api/news/external?query=${encodeURIComponent(query)}&targetLanguage=${targetLanguage}`),

  // Get news by category
  getNewsByCategory: (category, limit = 20, targetLanguage = 'en') => 
    api.get(`/api/news/dailyBulletin/${category}?limit=${limit}&targetLanguage=${targetLanguage}`),

  // Get all categories
  getAllCategories: () => 
    api.get('/api/news/viewCategories'),

  // Delete specific news
  deleteNews: (id) => 
    api.delete(`/api/news/deleteNews/${id}`),

  // Delete all news
  deleteAllNews: () => 
    api.delete('/api/news/deleteAll'),
};

export default api;
