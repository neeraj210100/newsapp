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
  Snackbar,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import moment from 'moment';
import axios from 'axios';

function ExternalNews() {
  const [query, setQuery] = useState('');
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;

    setLoading(true);
    setError(null);
    setSuccessMessage('');

    try {
      const response = await axios.get(`/api/news/external?query=${encodeURIComponent(query)}`);
      setNews(response.data);
      if (response.data.length === 0) {
        setError('No news found for your search term.');
      } else {
        setSuccessMessage(`Successfully fetched and saved ${response.data.length} news articles!`);
      }
    } catch (err) {
      setError('Failed to fetch external news. Please try again.');
      console.error('Error fetching external news:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSuccessMessage('');
  };

  return (
    <>
      <Typography variant="h4" component="h1" gutterBottom>
        External News Search
      </Typography>
      
      <Box component="form" onSubmit={handleSearch} sx={{ mb: 4 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={9}>
            <TextField
              fullWidth
              label="Search external news"
              variant="outlined"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Enter topic to fetch news..."
            />
          </Grid>
          <Grid item xs={12} sm={3}>
            <Button
              fullWidth
              variant="contained"
              type="submit"
              disabled={loading || !query.trim()}
              startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <SearchIcon />}
              sx={{ height: '56px' }}
            >
              Fetch & Save
            </Button>
          </Grid>
        </Grid>
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
                <Typography gutterBottom variant="h5" component="h2">
                  {item.title}
                </Typography>
                <Typography variant="body2" color="text.secondary" paragraph>
                  {item.description}
                </Typography>
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

      <Snackbar
        open={!!successMessage}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        message={successMessage}
      />
    </>
  );
}

export default ExternalNews; 