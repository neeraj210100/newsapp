import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Skeleton,
  Alert,
  IconButton,
  Fab,
  CircularProgress,
} from '@mui/material';
import { 
  Refresh as RefreshIcon, 
  Delete as DeleteIcon,
  Add as AddIcon 
} from '@mui/icons-material';
import { useSearchParams } from 'react-router-dom';
import { newsApi } from '../services/api';
import InstagramPost from './InstagramPost';
import { useTheme } from '../contexts/ThemeContext';

function NewsList({ 
  selectedCategory, 
  setSelectedCategory, 
  selectedLanguage, 
  setSelectedLanguage,
  categories,
  setCategories 
}) {
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchParams, setSearchParams] = useSearchParams();
  const { theme } = useTheme();

  useEffect(() => {
    fetchCategories();
    // Check for category parameter in URL
    const categoryFromUrl = searchParams.get('category');
    if (categoryFromUrl) {
      setSelectedCategory(categoryFromUrl);
    } else {
      fetchDailyNews();
    }
  }, []);

  useEffect(() => {
    if (selectedCategory) {
      fetchNewsByCategory();
      // Update URL with category parameter
      setSearchParams({ category: selectedCategory });
    } else {
      fetchDailyNews();
      // Remove category parameter from URL
      setSearchParams({});
    }
  }, [selectedCategory, selectedLanguage]);

  const fetchCategories = async () => {
    try {
      const response = await newsApi.getAllCategories();
      setCategories(response.data);
    } catch (err) {
      console.error('Error fetching categories:', err);
    }
  };

  const fetchDailyNews = async () => {
    setLoading(true);
    try {
      const response = await newsApi.getDailyNews(selectedLanguage);
      setNews(response.data);
      setError(null);
    } catch (err) {
      setError(err.message || 'Failed to fetch daily news. Please try again later.');
      console.error('Error fetching daily news:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchNewsByCategory = async () => {
    setLoading(true);
    try {
      const response = await newsApi.getNewsByCategory(selectedCategory, 20, selectedLanguage);
      setNews(response.data);
      setError(null);
    } catch (err) {
      setError(err.message || 'Failed to fetch news by category. Please try again later.');
      console.error('Error fetching news by category:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteNews = async (id) => {
    try {
      await newsApi.deleteNews(id);
      setNews(news.filter(item => item.id !== id));
    } catch (err) {
      setError(err.message || 'Failed to delete news item.');
      console.error('Error deleting news:', err);
    }
  };

  const handleDeleteAll = async () => {
    if (window.confirm('Are you sure you want to delete all news? This action cannot be undone.')) {
      try {
        await newsApi.deleteAllNews();
        setNews([]);
      } catch (err) {
        setError(err.message || 'Failed to delete all news.');
        console.error('Error deleting all news:', err);
      }
    }
  };

  const handleRefresh = () => {
    if (selectedCategory) {
      fetchNewsByCategory();
    } else {
      fetchDailyNews();
    }
  };

  if (loading) {
    return (
      <Box sx={{ 
        display: 'flex', 
        flexDirection: 'column', 
        alignItems: 'center', 
        gap: 3,
        py: 4 
      }}>
        {[1, 2, 3].map((item) => (
          <Box key={item} sx={{ width: '100%', maxWidth: 500 }}>
            <Skeleton variant="rectangular" height={400} sx={{ borderRadius: 2 }} />
            <Box sx={{ p: 2 }}>
              <Skeleton variant="text" height={40} />
              <Skeleton variant="text" height={20} />
              <Skeleton variant="text" height={20} />
            </Box>
          </Box>
        ))}
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <Alert severity="error" sx={{ maxWidth: 500 }}>
          {error}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ 
      minHeight: '100vh',
      backgroundColor: theme.palette.background.default,
      py: 2
    }}>
      {/* Header */}
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        mb: 3,
        px: 2
      }}>
        <Typography 
          variant="h4" 
          component="h1" 
          sx={{ 
            fontWeight: 600,
            color: theme.palette.text.primary
          }}
        >
          {selectedCategory ? `#${selectedCategory}` : 'News Feed'}
        </Typography>
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          <IconButton 
            onClick={handleRefresh} 
            color="primary" 
            title="Refresh"
            disabled={loading}
          >
            {loading ? <CircularProgress size={24} /> : <RefreshIcon />}
          </IconButton>
          <IconButton
            color="error"
            onClick={handleDeleteAll}
            title="Delete All"
          >
            <DeleteIcon />
          </IconButton>
        </Box>
      </Box>

      {/* Error Alert */}
      {error && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
          <Alert severity="error" sx={{ maxWidth: 500 }}>
            {error}
          </Alert>
        </Box>
      )}

      {/* Instagram-style Feed */}
      <Box sx={{ 
        display: 'flex', 
        flexDirection: 'column', 
        alignItems: 'center', 
        gap: 3,
        pb: 10
      }}>
        {news.map((item) => (
          <InstagramPost
            key={item.id}
            newsItem={item}
            onDelete={handleDeleteNews}
            theme={theme}
          />
        ))}
      </Box>

      {/* Empty State */}
      {news.length === 0 && !loading && !error && (
        <Box sx={{ 
          display: 'flex', 
          flexDirection: 'column',
          alignItems: 'center', 
          justifyContent: 'center',
          py: 8,
          textAlign: 'center'
        }}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No news articles found
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Try fetching some external news or check back later
          </Typography>
        </Box>
      )}

      {/* Floating Action Button for adding news */}
      <Fab
        color="primary"
        aria-label="add news"
        sx={{
          position: 'fixed',
          bottom: 16,
          right: 16,
          zIndex: 1000,
        }}
        onClick={handleRefresh}
        disabled={loading}
      >
        <AddIcon />
      </Fab>
    </Box>
  );
}

export default NewsList; 