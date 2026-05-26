# Supabase Connection Setup

This guide will help you connect the Traders Guardian backend to your Supabase PostgreSQL database.

## Step 1: Get Your Supabase Credentials

1. Go to [app.supabase.com](https://app.supabase.com)
2. Sign in with your account (or create one)
3. Select your **IT342_Oswa_TradersGuardian** project
4. In the left sidebar, click **Settings**
5. Click **Database** in the submenu
6. Under "Connection string", select **URI** tab
7. Copy the entire connection string (it should look like below)

The connection string format is:
```
postgresql://postgres.xxxxx:[YOUR-PASSWORD]@aws-1-ap-south-1.pooler.supabase.com:5432/postgres
```

## Step 2: Extract Your Credentials

From the connection string above, extract:

| Variable | Example | Where to Find |
|----------|---------|---------------|
| **SUPABASE_HOST** | `aws-1-ap-south-1.pooler.supabase.com` | Domain in connection string |
| **SUPABASE_USER** | `postgres.ixmvlhdqrcokeqhrdifr` | User before the colon `:` |
| **SUPABASE_PASSWORD** | `your_actual_password` | Password between `:` and `@` |
| **SUPABASE_PORT** | `5432` | Port number (usually 5432) |

## Step 3: Create .env File

1. In the project root directory, create a file named `.env`
2. Add your credentials:

```env
SUPABASE_HOST=aws-1-ap-south-1.pooler.supabase.com
SUPABASE_PORT=5432
SUPABASE_USER=postgres.ixmvlhdqrcokeqhrdifr
SUPABASE_PASSWORD=your_actual_password_here
```

**⚠️ IMPORTANT:** The `.env` file is already in `.gitignore` so it won't be committed to Git. This keeps your password safe.

## Step 4: Run the Application

```powershell
cd "c:\Oswa_Uf_VsCode\Traders Guardian\IT342_Oswa_TradersGuardian"
.\mvnw.cmd spring-boot:run
```

The application will now:
1. Load credentials from `.env` file
2. Connect to your Supabase PostgreSQL database
3. Auto-create the `users` table if it doesn't exist
4. Start Tomcat on `http://localhost:8080`

## Step 5: Test the Connection

### Option A: Using Browser
1. Open [http://localhost:8080/login](http://localhost:8080/login)
2. Try registering a new account
3. Check Supabase to see if the user was created

### Option B: Using curl or Postman
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstname": "Test",
    "lastname": "User"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## Troubleshooting

### "password authentication failed"
- Ensure `SUPABASE_PASSWORD` in `.env` is correct
- Copy the password directly from Supabase dashboard (no spaces)
- Restart the application after changing `.env`

### "Connection refused"
- Check that Supabase database is running
- Verify `SUPABASE_HOST` is correct
- Ensure your machine has internet access

### ".env file not loading"
- Restart IDE and terminal
- Ensure `.env` is in the project root (same folder as `pom.xml`)
- Check `.env` file has no BOM (Byte Order Mark)

## Production Deployment

For production, **never use .env files**. Instead:
1. Set environment variables on your server
2. Update `application.properties` to use those variables
3. Or use a separate `application-prod.properties` for production config

Example in your hosting platform:
```
Environment Variable: SUPABASE_PASSWORD = your_password
```

## Need Help?

- Supabase Docs: https://supabase.com/docs/guides/database/connecting-to-postgres
- Check Supabase Status: https://status.supabase.com
