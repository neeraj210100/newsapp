import React, { useState } from 'react';
import {
  Grid,
  Card,
  CardContent,
  CardMedia,
  Typography,
  CardActions,
  Button,
  TextField,
  Box,
  Alert,
  CircularProgress,
  Chip,
  IconButton,
} from '@mui/material';
import { Search as SearchIcon, Delete as DeleteIcon } from '@mui/icons-material';
import moment from 'moment';
import { newsApi } from '../services/api';

function SearchNews() {
  const [keyword, setKeyword] = useState('');
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

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
    <>
      <Typography variant="h4" component="h1" gutterBottom>
        Search News
      </Typography>
      
      <Box component="form" onSubmit={handleSearch} sx={{ mb: 4 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={9}>
            <TextField
              fullWidth
              label="Search news"
              variant="outlined"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="Enter keywords to search news..."
            />
          </Grid>
          <Grid item xs={12} sm={3}>
            <Button
              fullWidth
              variant="contained"
              type="submit"
              disabled={loading || !keyword.trim()}
              startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <SearchIcon />}
              sx={{ height: '56px' }}
            >
              Search
            </Button>
          </Grid>
        </Grid>
      </Box>

      {error && (
        <Alert severity="info" sx={{ mb: 2 }}>
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
    </>
  );
}

export default SearchNews; 