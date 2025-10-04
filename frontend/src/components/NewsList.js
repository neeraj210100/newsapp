import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  CardMedia,
  Typography,
  CardActions,
  Button,
  Skeleton,
  Alert,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  IconButton,
} from '@mui/material';
import { Delete as DeleteIcon, Refresh as RefreshIcon } from '@mui/icons-material';
import { useSearchParams } from 'react-router-dom';
import moment from 'moment';
import { newsApi } from '../services/api';

function NewsList() {
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedLanguage, setSelectedLanguage] = useState('en');
  const [searchParams, setSearchParams] = useSearchParams();

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
      <Grid container spacing={3}>
        {[1, 2, 3, 4].map((item) => (
          <Grid item xs={12} md={6} key={item}>
            <Card>
              <Skeleton variant="rectangular" height={140} />
              <CardContent>
                <Skeleton variant="text" height={40} />
                <Skeleton variant="text" height={20} />
                <Skeleton variant="text" height={20} />
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    );
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          {selectedCategory ? `${selectedCategory} News` : 'Daily News Bulletin'}
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <IconButton onClick={handleRefresh} color="primary" title="Refresh">
            <RefreshIcon />
          </IconButton>
          <Button
            variant="outlined"
            color="error"
            onClick={handleDeleteAll}
            startIcon={<DeleteIcon />}
            size="small"
          >
            Delete All
          </Button>
        </Box>
      </Box>

      <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
        <FormControl size="small" sx={{ minWidth: 150 }}>
          <InputLabel>Language</InputLabel>
          <Select
            value={selectedLanguage}
            label="Language"
            onChange={(e) => setSelectedLanguage(e.target.value)}
          >
            <MenuItem value="en">English</MenuItem>
            <MenuItem value="es">Spanish</MenuItem>
            <MenuItem value="fr">French</MenuItem>
            <MenuItem value="de">German</MenuItem>
            <MenuItem value="it">Italian</MenuItem>
            <MenuItem value="pt">Portuguese</MenuItem>
            <MenuItem value="ru">Russian</MenuItem>
            <MenuItem value="ja">Japanese</MenuItem>
            <MenuItem value="ko">Korean</MenuItem>
            <MenuItem value="zh">Chinese</MenuItem>
          </Select>
        </FormControl>

        <FormControl size="small" sx={{ minWidth: 150 }}>
          <InputLabel>Category</InputLabel>
          <Select
            value={selectedCategory}
            label="Category"
            onChange={(e) => setSelectedCategory(e.target.value)}
          >
            <MenuItem value="">All Categories</MenuItem>
            {categories.map((category) => (
              <MenuItem key={category} value={category}>
                {category}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        {news.map((item) => (
          <Grid item xs={12} md={6} key={item.id}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              {item.imageUrl && (
                <CardMedia
                  component="img"
                  height="200"
                  image={item.imageUrl}
                  alt={item.title}
                  sx={{ objectFit: 'cover' }}
                />
              )}
              <CardContent sx={{ flexGrow: 1 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                  <Typography gutterBottom variant="h5" component="h2" sx={{ flexGrow: 1 }}>
                    {item.title}
                  </Typography>
                  <IconButton
                    size="small"
                    color="error"
                    onClick={() => handleDeleteNews(item.id)}
                    title="Delete this news"
                  >
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                </Box>
                <Typography variant="body2" color="text.secondary" paragraph>
                  {item.description}
                </Typography>
                {item.category && (
                  <Chip
                    label={item.category}
                    size="small"
                    color="primary"
                    variant="outlined"
                    sx={{ mb: 1 }}
                  />
                )}
                <Typography variant="caption" color="text.secondary" display="block">
                  By {item.author || 'Unknown'} • {moment(item.publishedAt).format('MMMM D, YYYY')}
                </Typography>
              </CardContent>
              <CardActions>
                <Button size="small" color="primary" href={item.sourceUrl} target="_blank">
                  Read More
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      {news.length === 0 && !loading && !error && (
        <Alert severity="info">
          No news articles found. Try fetching some external news or check back later.
        </Alert>
      )}
    </>
  );
}

export default NewsList; 