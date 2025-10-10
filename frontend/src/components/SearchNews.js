import React, { useState } from 'react';
import {
  Typography,
  TextField,
  Box,
  Alert,
  CircularProgress,
  Button,
  Paper,
} from '@mui/material';
import { Search as SearchIcon } from '@mui/icons-material';
import { newsApi } from '../services/api';
import InstagramPost from './InstagramPost';
import { useTheme } from '../contexts/ThemeContext';

function SearchNews() {
  const [keyword, setKeyword] = useState('');
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const { theme } = useTheme();

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!keyword.trim()) return;

    setLoading(true);
    setError(null);

    try {
      const response = await newsApi.searchNews(keyword);
      setNews(response.data);
      if (response.data.length === 0) {
        setError('No news found for your search term.');
      }
    } catch (err) {
      setError(err.message || 'Failed to search news. Please try again.');
      console.error('Error searching news:', err);
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

  return (
    <Box sx={{ 
      minHeight: '100vh',
      backgroundColor: theme.palette.background.default,
      py: 2
    }}>
      {/* Header */}
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        mb: 4,
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
          Search News
        </Typography>
      </Box>
      
      {/* Search Form */}
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        mb: 4,
        px: 2
      }}>
        <Paper 
          component="form" 
          onSubmit={handleSearch} 
          sx={{ 
            p: 2, 
            display: 'flex', 
            alignItems: 'center', 
            gap: 2,
            maxWidth: 600,
            width: '100%',
            backgroundColor: theme.palette.background.paper,
            border: theme.palette.mode === 'dark' ? '1px solid #333' : '1px solid #e0e0e0',
          }}
        >
          <TextField
            fullWidth
            label="Search news"
            variant="outlined"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Enter keywords to search news..."
            size="small"
            sx={{
              '& .MuiOutlinedInput-root': {
                backgroundColor: theme.palette.background.default,
              }
            }}
          />
          <Button
            variant="contained"
            type="submit"
            disabled={loading || !keyword.trim()}
            startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <SearchIcon />}
            sx={{ 
              minWidth: 120,
              height: '40px'
            }}
          >
            Search
          </Button>
        </Paper>
      </Box>

      {/* Error Alert */}
      {error && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
          <Alert severity="info" sx={{ maxWidth: 500 }}>
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
      {news.length === 0 && !loading && !error && keyword && (
        <Box sx={{ 
          display: 'flex', 
          flexDirection: 'column',
          alignItems: 'center', 
          justifyContent: 'center',
          py: 8,
          textAlign: 'center'
        }}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No news found for "{keyword}"
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Try different keywords or check back later
          </Typography>
        </Box>
      )}
    </Box>
  );
}

export default SearchNews; 