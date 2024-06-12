import calendar
import datetime
from typing import List
from fastapi import FastAPI, Depends
from fastapi.middleware.cors import CORSMiddleware

from sqlalchemy.orm import Session

import models, crud, schemas
from database import SessionLocal, engine
models.Base.metadata.create_all(bind=engine)

from scraper import Scraper

app = FastAPI()
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

# Dependency
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

# api
@app.post('/tracked')
async def create_tracked(
        tracked: schemas.TrackedCreate,
        now: int = Depends(get_now),
        db: Session = Depends(get_db)
):
    return crud.create_tracked(db, tracked=tracked, now=now)


@app.get('/tracked',
         response_model=List[schemas.Tracked])
async def get_all(
        skip: int=0,
        limit: int=100,
        db: Session = Depends(get_db)
):
    return crud.get_all(db, skip=skip, limit=limit)


@app.get('/tracked/{id}')
async def get_one(
        id: int,
        db: Session = Depends(get_db)
):
    return crud.get_one(db, id)


@app.put('/tracked/{id}')
async def update_tracked(
        id: int,
        tracked: schemas.TrackedCreate,
        db: Session = Depends(get_db)
):
    
    db_tracked = crud.get_one(db, id)
    
    if not db_tracked:
        # raise exception
        pass

    return crud.update(db, id, tracked, db_tracked)


@app.put('/toggle/{id}')
async def toggle(
        id: int,
        db: Session = Depends(get_db)
):
    
    db_tracked = crud.get_one(db, id)
    
    if not db_tracked:
        # raise exception
        pass

    return crud.toggle(db, db_tracked)


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
    for db_tracked in db_tracked_list:
        if db_tracked.last_scraped != now:
            try:
                sc.run(db_tracked.name, db_tracked.sites)
            except Exception as e:
                print(e)
            else:
                db_tracked.last_scraped = now
                db.commit()

@app.post('/run_scraper/{id}')
async def run_scraper_one():
    pass

