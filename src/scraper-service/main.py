
import calendar
import datetime
import os

from typing import List

from fastapi import FastAPI, Depends, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware

from sqlalchemy import StaticPool, create_engine
from sqlalchemy.orm import Session
from sqlalchemy.orm import sessionmaker

import models, crud, schemas
from config import DATABASE_URL
from database import Base
from scraper import Scraper

app = FastAPI()

# Database Setup
ENV=os.getenv('ENV', 'test')
if ENV == 'development':
    engine = create_engine(DATABASE_URL)
elif ENV == 'test':
    engine = create_engine(
        DATABASE_URL,
        connect_args={"check_same_thread": False},
        poolclass=StaticPool
    )
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base.metadata.create_all(bind=engine)

# CORS Setup
origins = [
    "http://localhost",
    "http://localhost:3000"
]
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    #allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

# Dependencies
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def get_scraper():
    scraper = Scraper()
    return scraper


def get_now():
    t = datetime.datetime.now()
    date = datetime.datetime(t.year, t.month, t.day)
    date_epoch = calendar.timegm(date.timetuple())
    return date_epoch


# Routes
@app.get('/')
async def read_root():
    return {"message": "Hello World"}


@app.post('/tracked')
async def create_tracked(
        tracked: schemas.TrackedCreate,
        now: int = Depends(get_now),
        db: Session = Depends(get_db)
):
    # avoid nulls
    if tracked.name == '':
        raise HTTPException(status_code=422, detail="Name cannot be null")
    
    # avoid duplicates
    tracked.name = tracked.name.lower()
    db_tracked = crud.get_by_name(db, tracked.name)
    if db_tracked:
        raise HTTPException(status_code=409, detail="Name already exists")
    return crud.create_tracked(db, tracked=tracked, now=now)


@app.get('/tracked',
         response_model=List[schemas.Tracked])
async def get_all(
        skip: int=0,
        limit: int=100,
        db: Session = Depends(get_db)
):
    return crud.get_all(db, skip=skip, limit=limit)


@app.get('/tracked/{id}',
         response_model=schemas.Tracked)
async def get_one(
        id: int,
        db: Session = Depends(get_db)
):
    db_tracked = db.query(models.Tracked).filter(models.Tracked.id == id).first()
    if not db_tracked:
        raise HTTPException(status_code=404, detail="Tracked item not found")
    return db_tracked


@app.put('/tracked/{id}')
async def update_tracked(
        id: int,
        tracked: schemas.TrackedCreate,
        db: Session = Depends(get_db)
):

    db_tracked = crud.get_one(db, id)
    
    if db_tracked:
        raise HTTPException(status_code=201, detail="Tracked item already exists")

    return crud.update(db, id, tracked, db_tracked)


@app.put('/toggle/{id}')
async def toggle(
        id: int,
        db: Session = Depends(get_db)
):
    
    db_tracked = crud.get_one(db, id)
    
    if db_tracked is None:
        raise HTTPException(status_code=404, detail="Tracked item not found")

    crud.toggle(db, db_tracked)
    return {'is_active': db_tracked.is_active}


@app.delete('/tracked/{id}')
async def delete_tracked(
        id: int,
        db: Session = Depends(get_db)
):
    crud.delete(db, id)


@app.post('/run_scraper')
async def run_scraper_all(
    db: Session = Depends(get_db),
    sc: Scraper = Depends(get_scraper),
    now: int = Depends(get_now)
):
    db_tracked_list = crud.get_all_by_is_active(db)
    scraping_done = False
    for db_tracked in db_tracked_list:
        if (datetime.datetime.fromtimestamp(db_tracked.last_scraped).strftime('%Y-%m-%d') != \
            datetime.datetime.fromtimestamp(now).strftime('%Y-%m-%d')) :
            try:
                sc.run(db_tracked.name, db_tracked.sites)
            except Exception as e:
                print(e)
            else:
                db_tracked.last_scraped = now
                scraping_done = True
                db.commit()
        else:
            pass
    
    if not scraping_done:
        raise HTTPException(status_code=400, detail={'message': 'No new data to scrape'})
    else:
        raise HTTPException(status_code=200, detail={'message': 'Scraping completed successfully'})

