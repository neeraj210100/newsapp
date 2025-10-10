import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import CssBaseline from '@mui/material/CssBaseline';
import { Box } from '@mui/material';
import { ThemeProvider } from './contexts/ThemeContext';
import Navbar from './components/Navbar';
import NewsList from './components/NewsList';
import SearchNews from './components/SearchNews';
import { newsApi } from './services/api';

function App() {
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedLanguage, setSelectedLanguage] = useState('en');
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await newsApi.getAllCategories();
      setCategories(response.data);
    } catch (err) {
      console.error('Error fetching categories:', err);
    }
  };

  const handleLanguageChange = (language) => {
    setSelectedLanguage(language);
  };

  const handleCategoryChange = (category) => {
    setSelectedCategory(category);
  };

  return (
    <ThemeProvider>
      <CssBaseline />
      <Router>
        <Box sx={{ 
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column'
        }}>
          <Navbar 
            selectedLanguage={selectedLanguage}
            onLanguageChange={handleLanguageChange}
            selectedCategory={selectedCategory}
            onCategoryChange={handleCategoryChange}
            categories={categories}
          />
          <Box sx={{ flexGrow: 1 }}>
            <Routes>
              <Route 
                path="/" 
                element={
                  <NewsList 
                    selectedCategory={selectedCategory}
                    setSelectedCategory={setSelectedCategory}
                    selectedLanguage={selectedLanguage}
                    setSelectedLanguage={setSelectedLanguage}
                    categories={categories}
                    setCategories={setCategories}
                  />
                } 
              />
              <Route path="/search" element={<SearchNews />} />
            </Routes>
          </Box>
        </Box>
      </Router>
    </ThemeProvider>
  );
}

export default App; 