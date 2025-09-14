import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import NewspaperIcon from '@mui/icons-material/Newspaper';

function Navbar() {
  return (
    <AppBar position="static">
      <Toolbar>
        <NewspaperIcon sx={{ mr: 2 }} />
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          News Portal
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            color="inherit"
            component={RouterLink}
            to="/"
          >
            Daily News
          </Button>
          <Button
            color="inherit"
            component={RouterLink}
            to="/search"
          >
            Search
          </Button>
          <Button
            color="inherit"
            component={RouterLink}
            to="/external"
          >
            External News
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar; 