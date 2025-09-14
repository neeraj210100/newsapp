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
} from '@mui/material';
import moment from 'moment';
import axios from 'axios';

function NewsList() {
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDailyNews();
  }, []);

  const fetchDailyNews = async () => {
    try {
      const response = await axios.get('/api/news/dailyBulletin');
      setNews(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch daily news. Please try again later.');
      console.error('Error fetching daily news:', err);
    } finally {
      setLoading(false);
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
      <Typography variant="h4" component="h1" gutterBottom>
        Daily News Bulletin
      </Typography>
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
    </>
  );
}

export default NewsList; 