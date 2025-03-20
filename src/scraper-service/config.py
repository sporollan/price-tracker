# config.py
import os
from sqlalchemy import StaticPool, create_engine
from sqlalchemy.orm import sessionmaker
import logging

ENV = os.getenv("ENV", "test")
DB_HOST = os.getenv("SCRAPER_DB_HOST", "scraper-db")

# Define the database URL without creating the engine
def get_database_url():
    if ENV == "development":
        return f"postgresql://postgres:mysecretpassword@{DB_HOST}:5432/postgres"
    elif ENV == "test":
        return "sqlite:///:memory:"

DATABASE_URL = get_database_url()

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)