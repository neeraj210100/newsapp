# News Application

## Cloud Database Setup

### Option 1: Google Cloud SQL

1. Create a Google Cloud SQL instance:
   - Go to [Google Cloud Console](https://console.cloud.google.com)
   - Navigate to SQL
   - Click "Create Instance"
   - Choose MySQL
   - Configure basic settings:
     - Instance ID: `news-db`
     - Password: (set a strong password)
     - Region: Choose nearest to you
     - Machine type: Lightweight (dev/test)
     - Storage: 10GB (can increase later)

2. Configure database:
   - Create database: `news_db`
   - Create user with appropriate permissions

3. Set up environment variables:
   ```bash
   export CLOUD_SQL_HOST=your-instance-ip
   export CLOUD_SQL_DATABASE=news_db
   export CLOUD_SQL_USER=your-username
   export CLOUD_SQL_PASSWORD=your-password
   ```

### Security Best Practices

1. Never commit sensitive information to version control
2. Use environment variables or secret management service
3. Enable SSL for database connections
4. Restrict database access to specific IP ranges
5. Use strong passwords
6. Regularly backup your database
7. Monitor database metrics and set up alerts

### Local Development

Create a `.env.development` file (do not commit to git):
```properties
CLOUD_SQL_HOST=your-instance-ip
CLOUD_SQL_DATABASE=news_db
CLOUD_SQL_USER=your-username
CLOUD_SQL_PASSWORD=your-password
```

### Production Deployment

1. Use environment variables in production
2. Consider using cloud secret management:
   - Google Cloud Secret Manager
   - AWS Secrets Manager
   - Kubernetes Secrets

### Database Migration

To migrate your local data to cloud:

1. Export local database:
   ```bash
   mysqldump -u root -p demo > backup.sql
   ```

2. Import to cloud database:
   ```bash
   mysql -h $CLOUD_SQL_HOST -u $CLOUD_SQL_USER -p $CLOUD_SQL_DATABASE < backup.sql
   ```

### Monitoring and Maintenance

1. Set up monitoring:
   - Database metrics
   - Query performance
   - Storage usage
   - Connection count

2. Regular maintenance:
   - Update database version
   - Optimize queries
   - Clean up old data
   - Check backup integrity 

#Swagger
http://localhost:8080/swagger-ui/index.html