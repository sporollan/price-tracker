
import asyncio
import datetime
import models
import crud
import schemas

from config import logger
from fastapi import HTTPException
from sqlalchemy.orm import Session
from scraper import Scraper

scraper_locks = {}

# Services
def read_root():
    return {"message": "Hello World"}


def create_tracked(
        tracked: schemas.TrackedCreate,
        now: int,
        db: Session,
        sc: Scraper
):
    # avoid nulls
    if tracked.name == '' or tracked.users == '':
        raise HTTPException(status_code=422, detail="Name cannot be null")
    
    # get or create user
    db_user = crud.get_user_by_id(db, tracked.users[0])
    if not db_user:
        db_user = crud.create_user(db, tracked.users[0])

    # avoid duplicates
    tracked.name = tracked.name.lower()
    db_tracked = crud.get_by_name(db, tracked.name)
    if db_tracked:
        # add user to tracked
        if db_tracked in db_user.tracked_items:
            raise HTTPException(status_code=409, detail="Name already exists")
        db_user.tracked_items.append(db_tracked)
        db.commit()
        db.refresh(db_user)
        return db_tracked
    db_tracked = crud.create_tracked(db, tracked=tracked, user_id=tracked.users[0], now=now)
    asyncio.create_task(scrape_item(db_tracked, db, sc, now))
    return db_tracked


def get_all(
        user: str,
        db: Session,
        skip: int=0,
        limit: int=100,
):
    return crud.get_all(db, user, skip=skip, limit=limit)


def get_one(
        id: int,
        db: Session
):
    db_tracked = db.query(models.Tracked).filter(models.Tracked.id == id).first()
    if not db_tracked:
        raise HTTPException(status_code=404, detail="Tracked item not found")
    return db_tracked


def update_tracked(
        id: int,
        tracked: schemas.TrackedCreate,
        db: Session
):

    db_tracked = crud.get_one(db, id)
    
    if db_tracked:
        raise HTTPException(status_code=201, detail="Tracked item already exists")

    return crud.update(db, id, tracked, db_tracked)


def remove_tracked(
        user: str,
        id: int,
        db: Session
):
    logger.info(f"Removing {id} from {user}")
    crud.remove_from_user(db, id, user)
    return {'is_active': False}


def delete_tracked(
        id: int,
        db: Session
):
    crud.delete(db, id)


async def scrape_item(db_tracked, task_db, sc, now):
        global scraper_locks

        if db_tracked.name not in scraper_locks:
            scraper_locks[db_tracked.name] = asyncio.Lock()

        # Prevent duplicate scraping if two users request same item
        async with scraper_locks[db_tracked.name]:
            if (datetime.datetime.fromtimestamp(db_tracked.last_scraped).strftime('%Y-%m-%d') == \
                datetime.datetime.fromtimestamp(now).strftime('%Y-%m-%d')) :
                return False
            try:
                logger.info(f"Starting Scraper for {db_tracked.name}")

                # Create db session to ensure atomic db transactions regarding last_scraped
                await asyncio.to_thread(sc.run, db_tracked.name, db_tracked.sites)
                # Refresh db_tracked
                tracked_item = crud.get_one(task_db, db_tracked.id)
                if tracked_item:
                    crud.update_last_scraped(task_db, tracked_item, now)
                return True
            except Exception as e:
                logger.error(e)
                return False


async def scrape_all(db_tracked_list, db, sc, now):
    scraping_done = False
    for db_tracked in db_tracked_list:
        scraping_done = await scrape_item(db_tracked, db, sc, now) or scraping_done
    return scraping_done


def run_scraper_all(
    db: Session,
    sc: Scraper,
    now: int
):
    logger.info("Running Scraper for All Elements")
    db_tracked_list = crud.get_tracked_all(db)
    if(db_tracked_list):
        asyncio.create_task(scrape_all(db_tracked_list, db, sc, now))
        raise HTTPException(status_code=200, detail={'message': 'Scraping Started Successfully'})
    raise HTTPException(status_code=200, detail={'message': 'Scraping up to Date'})


def run_scraper_one(
    id: int,
    db: Session,
    sc: Scraper,
    now: int
):
    db_tracked = crud.get_one(db, id)
    logger.info(f"Running Scraper for {id}")
    scraping_done = scrape_item(db_tracked, db, sc, now)

    if not scraping_done:
        raise HTTPException(status_code=200, detail={'message': 'Scraping up to Date'})
    else:
        raise HTTPException(status_code=200, detail={'message': 'Scraping Started Successfully'})

