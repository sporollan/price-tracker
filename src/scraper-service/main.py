import schemas
import service
import config
import database

from scraper import Scraper
from typing import List
from fastapi import FastAPI, Depends, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
from dependency import get_db, get_now, get_scraper
from sqlalchemy.orm import Session


@asynccontextmanager
async def lifespan(app: FastAPI):
    # --- Startup: create tables ---
    database.init_db()
    yield


app = FastAPI(lifespan=lifespan)
app.add_middleware(
    CORSMiddleware,
    allow_origins=config.origins,
    #allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)


# Routes
@app.get('/')
async def read_root():
    return {"message": "Hello World"}


@app.post('/tracked')
async def create_tracked(
        tracked: schemas.TrackedCreate,
        now: int = Depends(get_now),
        db: Session = Depends(get_db),
        sc: Scraper = Depends(get_scraper)
):
    return service.create_tracked(tracked, now, db, sc)


@app.get('/tracked',
         response_model=List[schemas.Tracked])
async def get_all(
        user: str,
        db: Session = Depends(get_db),
        skip: int=0,
        limit: int=100,

):
    return service.get_all(user=user, skip=skip, limit=limit, db=db)


@app.get('/tracked/{id}',
         response_model=schemas.Tracked)
async def get_one(
        id: int,
        db: Session = Depends(get_db)
):
    return service.get_one(id=id, db=db)


@app.put('/tracked/{id}')
async def update_tracked(
        id: int,
        tracked: schemas.TrackedCreate,
        db: Session = Depends(get_db)
):
    return service.update_tracked(id=id, tracked=tracked, db=db)


@app.put('/toggle')
async def remove_tracked(
        user: str,
        id: int,
        db: Session = Depends(get_db)
):
    return service.remove_tracked(user=user, id=id, db=db)


@app.delete('/tracked/{id}')
async def delete_tracked(
        id: int,
        db: Session = Depends(get_db)
):
    return service.delete_tracked(id=id, db=db)


@app.post('/run_scraper')
async def run_scraper_all(
    db: Session = Depends(get_db),
    sc: Scraper = Depends(get_scraper),
    now: int = Depends(get_now)
):
    return service.run_scraper_all(db=db, sc=sc, now=now)


@app.post('/run_scraper/{item}')
async def run_scraper_one(
    id: int,
    db: Session = Depends(get_db),
    sc: Scraper = Depends(get_scraper),
    now: int = Depends(get_now)
):
    return service.run_scraper_one(id=id, db=db, sc=sc, now=now)

# health and readiness
@app.get("/liveness", status_code=200)
def health_check():
    return {"status": "ok"}

@app.get("/readiness")
def readiness_check():
    try:
        # Check DB, cache, etc.
        return {"status": "ready"}
    except Exception as e:
        raise HTTPException(status_code=503, detail={"status": "not ready", "reason": str(e)})