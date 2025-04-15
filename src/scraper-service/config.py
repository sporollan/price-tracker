# config.py
import os
from sqlalchemy import StaticPool, create_engine
from sqlalchemy.orm import sessionmaker
import logging

ENV = os.getenv("ENV", "test")
DB_HOST = os.getenv("DB_HOST", "")
DB_USER = os.getenv("TRACKED_USER", "")
DB_PASSWORD = os.getenv("DB_PASSWORD", "")
DB_NAME = os.getenv("TRACKED_DB", "")

# Define the database URL without creating the engine
def get_database_url():
    if ENV == "dev" or ENV == 'prod':
#        return f"postgresql://user:password@{DB_HOST}:5432/tracked"
        return f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:5432/{DB_NAME}"
    elif ENV == "test":
        return "sqlite:///:memory:"

DATABASE_URL = get_database_url()

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)