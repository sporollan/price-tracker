
import os

from sqlalchemy.orm import declarative_base
from sqlalchemy import StaticPool, create_engine
from sqlalchemy.orm import sessionmaker
from config import DATABASE_URL

Base = declarative_base()

# Database Setup
ENV=os.getenv('ENV', 'test')
if ENV == 'dev' or ENV == 'prod':
    engine = create_engine(DATABASE_URL)
elif ENV == 'test':
    engine = create_engine(
        DATABASE_URL,
        connect_args={"check_same_thread": False},
        poolclass=StaticPool
    )

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def init_db():
    Base.metadata.create_all(bind=engine)
