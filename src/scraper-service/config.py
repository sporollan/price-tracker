import os
DB_HOST = os.getenv("SCRAPER_DB_HOST", "scraper_db")
SQLALCHEMY_DATABASE_URL = f"postgresql://postgres:mysecretpassword@{DB_HOST}:5432/postgres"