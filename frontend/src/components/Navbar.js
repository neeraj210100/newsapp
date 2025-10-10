import React, { useState } from 'react';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  Box, 
  IconButton, 
  Menu, 
  MenuItem, 
  FormControl,
  Select,
  Tooltip
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import {
  Newspaper as NewspaperIcon,
  DarkMode as DarkModeIcon,
  LightMode as LightModeIcon,
  Language as LanguageIcon,
  FilterList as FilterIcon,
} from '@mui/icons-material';
import { useTheme } from '../contexts/ThemeContext';

function Navbar({ selectedLanguage, onLanguageChange, selectedCategory, onCategoryChange, categories }) {
  const { darkMode, toggleTheme } = useTheme();
  const [languageAnchor, setLanguageAnchor] = useState(null);
  const [categoryAnchor, setCategoryAnchor] = useState(null);

  const handleLanguageMenuOpen = (event) => {
    setLanguageAnchor(event.currentTarget);
  };

  const handleLanguageMenuClose = () => {
    setLanguageAnchor(null);
  };

  const handleCategoryMenuOpen = (event) => {
    setCategoryAnchor(event.currentTarget);
  };

  const handleCategoryMenuClose = () => {
    setCategoryAnchor(null);
  };

  const handleLanguageSelect = (language) => {
    onLanguageChange(language);
    handleLanguageMenuClose();
  };

  const handleCategorySelect = (category) => {
    onCategoryChange(category);
    handleCategoryMenuClose();
  };

  const languages = [
    { code: 'en', name: 'English', flag: '🇺🇸' },
    { code: 'es', name: 'Español', flag: '🇪🇸' },
    { code: 'fr', name: 'Français', flag: '🇫🇷' },
    { code: 'de', name: 'Deutsch', flag: '🇩🇪' },
    { code: 'it', name: 'Italiano', flag: '🇮🇹' },
    { code: 'pt', name: 'Português', flag: '🇵🇹' },
    { code: 'ru', name: 'Русский', flag: '🇷🇺' },
    { code: 'ja', name: '日本語', flag: '🇯🇵' },
    { code: 'ko', name: '한국어', flag: '🇰🇷' },
    { code: 'zh', name: '中文', flag: '🇨🇳' },
  ];

  const currentLanguage = languages.find(lang => lang.code === selectedLanguage);

  return (
    <AppBar position="static" elevation={1}>
      <Toolbar>
        <NewspaperIcon sx={{ mr: 2 }} />
        <Typography variant="h6" component="div" sx={{ flexGrow: 1, fontWeight: 600 }}>
          NewsGram
        </Typography>
        
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          {/* Navigation Buttons */}
          <Button
            color="inherit"
            component={RouterLink}
            to="/"
            sx={{ 
              fontWeight: 500,
              '&:hover': {
                backgroundColor: 'rgba(255,255,255,0.1)'
              }
            }}
          >
            Feed
          </Button>
          <Button
            color="inherit"
            component={RouterLink}
            to="/search"
            sx={{ 
              fontWeight: 500,
              '&:hover': {
                backgroundColor: 'rgba(255,255,255,0.1)'
              }
            }}
          >
            Search
          </Button>

          {/* Category Filter */}
          <Tooltip title="Filter by Category">
            <IconButton
              color="inherit"
              onClick={handleCategoryMenuOpen}
              sx={{ 
                ml: 1,
                '&:hover': {
                  backgroundColor: 'rgba(255,255,255,0.1)'
                }
              }}
            >
              <FilterIcon />
            </IconButton>
          </Tooltip>

          {/* Language Selector */}
          <Tooltip title="Change Language">
            <IconButton
              color="inherit"
              onClick={handleLanguageMenuOpen}
              sx={{ 
                ml: 1,
                '&:hover': {
                  backgroundColor: 'rgba(255,255,255,0.1)'
                }
              }}
            >
              <LanguageIcon />
            </IconButton>
          </Tooltip>

          {/* Theme Toggle */}
          <Tooltip title={darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}>
            <IconButton
              color="inherit"
              onClick={toggleTheme}
              sx={{ 
                ml: 1,
                '&:hover': {
                  backgroundColor: 'rgba(255,255,255,0.1)'
                }
              }}
            >
              {darkMode ? <LightModeIcon /> : <DarkModeIcon />}
            </IconButton>
          </Tooltip>
        </Box>
      </Toolbar>

      {/* Language Menu */}
      <Menu
        anchorEl={languageAnchor}
        open={Boolean(languageAnchor)}
        onClose={handleLanguageMenuClose}
        PaperProps={{
          sx: {
            maxHeight: 300,
            width: 200,
          }
        }}
      >
        {languages.map((language) => (
          <MenuItem
            key={language.code}
            onClick={() => handleLanguageSelect(language.code)}
            selected={selectedLanguage === language.code}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <span style={{ fontSize: '1.2em' }}>{language.flag}</span>
              <Typography variant="body2">{language.name}</Typography>
            </Box>
          </MenuItem>
        ))}
      </Menu>

      {/* Category Menu */}
      <Menu
        anchorEl={categoryAnchor}
        open={Boolean(categoryAnchor)}
        onClose={handleCategoryMenuClose}
        PaperProps={{
          sx: {
            maxHeight: 300,
            width: 200,
          }
        }}
      >
        <MenuItem
          onClick={() => handleCategorySelect('')}
          selected={selectedCategory === ''}
        >
          <Typography variant="body2">All Categories</Typography>
        </MenuItem>
        {categories.map((category) => (
          <MenuItem
            key={category}
            onClick={() => handleCategorySelect(category)}
            selected={selectedCategory === category}
          >
            <Typography variant="body2">{category}</Typography>
          </MenuItem>
        ))}
      </Menu>
    </AppBar>
  );
}

export default Navbar; 