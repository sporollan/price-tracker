from typing import List
from fastapi import FastAPI, Depends
from fastapi.middleware.cors import CORSMiddleware

from sqlalchemy.orm import Session

import models, crud, schemas
from database import SessionLocal, engine
models.Base.metadata.create_all(bind=engine)


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


@app.post('/tracked')
def create_tracked(
        tracked: schemas.TrackedCreate,
        db: Session = Depends(get_db)
):
    return crud.create_tracked(db, tracked=tracked)


@app.get('/tracked',
         response_model=List[schemas.Tracked])
def get_all(
        skip: int=0,
        limit: int=100,
        db: Session = Depends(get_db)
):
    return crud.get_all(db, skip=skip, limit=limit)


@app.get('/tracked/{id}')
def get_one(
        id: int,
        db: Session = Depends(get_db)
):
    return crud.get_one(db, id)


@app.put('/tracked/{id}')
def update_tracked(
        id: int,
        tracked: schemas.TrackedCreate,
        db: Session = Depends(get_db)
):
    
    db_tracked = db.query(models.Tracked) \
                    .filter(models.Tracked.id == id) \
                    .first()
    
    if not db_tracked:
        # raise exception
        pass

    return crud.update(db, id, tracked, db_tracked)


@app.delete('/tracked/{id}')
def delete_tracked(
        id: int,
        db: Session = Depends(get_db)
):
    crud.delete(db, id)


@app.post('/run_scraper')
def run_scraper_all():
    pass


@app.post('/run_scraper/{id}')
def run_scraper_one():
    pass

