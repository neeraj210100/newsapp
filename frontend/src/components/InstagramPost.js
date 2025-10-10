import React, { useState } from 'react';
import {
  Card,
  CardHeader,
  Avatar,
  IconButton,
  CardMedia,
  CardContent,
  Typography,
  Box,
  Chip,
  Button,
  Menu,
  MenuItem,
  Tooltip,
} from '@mui/material';
import {
  MoreVert as MoreVertIcon,
  Favorite as FavoriteIcon,
  FavoriteBorder as FavoriteBorderIcon,
  Share as ShareIcon,
  BookmarkBorder as BookmarkBorderIcon,
  Bookmark as BookmarkIcon,
  ChatBubbleOutline as ChatBubbleOutlineIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import moment from 'moment';

function InstagramPost({ newsItem, onDelete, theme }) {
  const [liked, setLiked] = useState(false);
  const [saved, setSaved] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [likesCount, setLikesCount] = useState(Math.floor(Math.random() * 1000) + 10);

  const handleLike = () => {
    setLiked(!liked);
    setLikesCount(prev => liked ? prev - 1 : prev + 1);
  };

  const handleSave = () => {
    setSaved(!saved);
  };

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleDelete = () => {
    onDelete(newsItem.id);
    handleMenuClose();
  };

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: newsItem.title,
        text: newsItem.description,
        url: newsItem.sourceUrl,
      });
    } else {
      navigator.clipboard.writeText(newsItem.sourceUrl);
    }
    handleMenuClose();
  };

  const getInitials = (name) => {
    if (!name) return 'N';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  };

  const getRandomColor = (str) => {
    const colors = [
      '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7',
      '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9'
    ];
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    return colors[Math.abs(hash) % colors.length];
  };

  return (
    <Card 
      sx={{ 
        maxWidth: 500, 
        mx: 'auto', 
        mb: 3,
        backgroundColor: theme.palette.mode === 'dark' ? '#1a1a1a' : '#ffffff',
        border: theme.palette.mode === 'dark' ? '1px solid #333' : '1px solid #e0e0e0',
        borderRadius: 2,
        boxShadow: theme.palette.mode === 'dark' 
          ? '0 2px 8px rgba(0,0,0,0.3)' 
          : '0 2px 8px rgba(0,0,0,0.1)',
      }}
    >
      {/* Header */}
      <CardHeader
        avatar={
          <Avatar 
            sx={{ 
              bgcolor: getRandomColor(newsItem.author || 'News'),
              width: 32,
              height: 32,
              fontSize: '0.875rem'
            }}
          >
            {getInitials(newsItem.author || 'News')}
          </Avatar>
        }
        action={
          <IconButton onClick={handleMenuOpen}>
            <MoreVertIcon />
          </IconButton>
        }
        title={
          <Typography variant="subtitle2" sx={{ fontWeight: 600, fontSize: '0.875rem' }}>
            {newsItem.author || 'News Source'}
          </Typography>
        }
        subheader={
          <Typography variant="caption" color="text.secondary">
            {moment(newsItem.publishedAt).fromNow()}
          </Typography>
        }
        sx={{ 
          pb: 1,
          '& .MuiCardHeader-content': {
            overflow: 'hidden'
          }
        }}
      />

      {/* Image */}
      {newsItem.imageUrl && (
        <CardMedia
          component="img"
          height="400"
          image={newsItem.imageUrl}
          alt={newsItem.title}
          sx={{ 
            objectFit: 'cover',
            cursor: 'pointer',
            '&:hover': {
              opacity: 0.95
            }
          }}
          onClick={() => window.open(newsItem.sourceUrl, '_blank')}
        />
      )}

      {/* Action Buttons */}
      <Box sx={{ display: 'flex', alignItems: 'center', p: 1, gap: 1 }}>
        <Tooltip title={liked ? 'Unlike' : 'Like'}>
          <IconButton onClick={handleLike} size="small">
            {liked ? (
              <FavoriteIcon sx={{ color: '#e91e63' }} />
            ) : (
              <FavoriteBorderIcon />
            )}
          </IconButton>
        </Tooltip>
        
        <Tooltip title="Comment">
          <IconButton size="small">
            <ChatBubbleOutlineIcon />
          </IconButton>
        </Tooltip>
        
        <Tooltip title="Share">
          <IconButton onClick={handleShare} size="small">
            <ShareIcon />
          </IconButton>
        </Tooltip>
        
        <Box sx={{ flexGrow: 1 }} />
        
        <Tooltip title={saved ? 'Unsave' : 'Save'}>
          <IconButton onClick={handleSave} size="small">
            {saved ? (
              <BookmarkIcon />
            ) : (
              <BookmarkBorderIcon />
            )}
          </IconButton>
        </Tooltip>
      </Box>

      {/* Likes Count */}
      <Box sx={{ px: 2, pb: 1 }}>
        <Typography variant="subtitle2" sx={{ fontWeight: 600, fontSize: '0.875rem' }}>
          {likesCount.toLocaleString()} likes
        </Typography>
      </Box>

      {/* Content */}
      <CardContent sx={{ pt: 0, pb: 2 }}>
        <Typography variant="body2" sx={{ mb: 1 }}>
          <Typography component="span" sx={{ fontWeight: 600, fontSize: '0.875rem' }}>
            {newsItem.author || 'News Source'}
          </Typography>
          {' '}
          <Typography component="span">
            {newsItem.description}
          </Typography>
        </Typography>
        
        {newsItem.category && (
          <Chip
            label={`#${newsItem.category.toLowerCase().replace(/\s+/g, '')}`}
            size="small"
            sx={{ 
              mr: 1, 
              mb: 1,
              backgroundColor: theme.palette.mode === 'dark' ? '#333' : '#f0f0f0',
              color: theme.palette.mode === 'dark' ? '#fff' : '#000',
              fontSize: '0.75rem'
            }}
          />
        )}
        
        <Typography 
          variant="caption" 
          color="text.secondary" 
          sx={{ 
            display: 'block', 
            mt: 1,
            cursor: 'pointer',
            '&:hover': {
              textDecoration: 'underline'
            }
          }}
          onClick={() => window.open(newsItem.sourceUrl, '_blank')}
        >
          Read more...
        </Typography>
      </CardContent>

      {/* Menu */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleShare}>
          <ShareIcon sx={{ mr: 1 }} fontSize="small" />
          Share
        </MenuItem>
        <MenuItem onClick={handleDelete} sx={{ color: 'error.main' }}>
          <DeleteIcon sx={{ mr: 1 }} fontSize="small" />
          Delete
        </MenuItem>
      </Menu>
    </Card>
  );
}

export default InstagramPost;
