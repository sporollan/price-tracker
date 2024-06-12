
import datetime, calendar

from sqlalchemy.orm import Session

import models, schemas

def create_tracked(
        db: Session,
        tracked: schemas.TrackedCreate,
        now: int
):
    
    db_tracked = models.Tracked(
        name=tracked.name,
        sites=tracked.sites,
        date_added=now,
        last_scraped=0,
        is_active=True
    )

    db.add(db_tracked)
    db.commit()
    db.refresh(db_tracked)
    return db_tracked

def get_one(
        db: Session,
        id: int
):
    return db.query(models.Tracked) \
            .filter(models.Tracked.id == id) \
            .first()

def get_all(
        db: Session,
        skip: int,
        limit: int
):
    return db.query(models.Tracked) \
            .offset(skip) \
            .limit(limit) \
            .all()

def update(
        db: Session,
        id: int,
        tracked: schemas.Tracked,
        db_tracked: schemas.Tracked
):
    db_tracked.name = tracked.name
    db_tracked.sites = tracked.sites
    
    db.commit()


def toggle(
        db: Session,
        db_tracked: schemas.Tracked
):
    db_tracked.is_active = not db_tracked.is_active
    db.commit()

    return db_tracked


def delete(
        db: Session,
        id: int
):
    db.query(models.Tracked) \
            .filter(models.Tracked.id == id) \
            .delete()
    db.commit()


def get_all_by_is_active(
        db: Session
):
    return db.query(models.Tracked) \
                .filter(models.Tracked.is_active) \
                .all()
