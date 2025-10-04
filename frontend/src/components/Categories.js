import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Box,
  Alert,
  Skeleton,
  Chip,
} from '@mui/material';
import { Category as CategoryIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { newsApi } from '../services/api';

function Categories() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await newsApi.getAllCategories();
      setCategories(response.data);
      setError(null);
    } catch (err) {
      setError(err.message || 'Failed to fetch categories. Please try again later.');
      console.error('Error fetching categories:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCategoryClick = (category) => {
    // Navigate to home page with category filter
    navigate(`/?category=${encodeURIComponent(category)}`);
  };

  if (loading) {
    return (
      <Box>
        <Typography variant="h4" component="h1" gutterBottom>
          News Categories
        </Typography>
        <Grid container spacing={3}>
          {[1, 2, 3, 4, 5, 6].map((item) => (
            <Grid item xs={12} sm={6} md={4} key={item}>
              <Card>
                <CardContent>
                  <Skeleton variant="text" height={40} />
                  <Skeleton variant="text" height={20} />
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>
    );
  }

  if (error) {
    return (
      <Box>
        <Typography variant="h4" component="h1" gutterBottom>
          News Categories
        </Typography>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        News Categories
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        Browse news by category. Click on any category to view related articles.
      </Typography>

      {categories.length === 0 ? (
        <Alert severity="info">
          No categories available. Try fetching some external news to populate categories.
        </Alert>
      ) : (
        <Grid container spacing={3}>
          {categories.map((category, index) => (
            <Grid item xs={12} sm={6} md={4} key={category}>
              <Card 
                sx={{ 
                  height: '100%', 
                  display: 'flex', 
                  flexDirection: 'column',
                  cursor: 'pointer',
                  transition: 'transform 0.2s, box-shadow 0.2s',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: 4,
                  }
                }}
                onClick={() => handleCategoryClick(category)}
              >
                <CardContent sx={{ flexGrow: 1, textAlign: 'center', py: 4 }}>
                  <CategoryIcon 
                    sx={{ 
                      fontSize: 48, 
                      color: 'primary.main', 
                      mb: 2 
                    }} 
                  />
                  <Typography variant="h5" component="h2" gutterBottom>
                    {category}
                  </Typography>
                  <Chip
                    label={`Category ${index + 1}`}
                    size="small"
                    color="primary"
                    variant="outlined"
                  />
                  <Box sx={{ mt: 2 }}>
                    <Button
                      variant="contained"
                      color="primary"
                      size="small"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleCategoryClick(category);
                      }}
                    >
                      View Articles
                    </Button>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
}

export default Categories;
