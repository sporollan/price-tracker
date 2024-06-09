
import datetime, calendar

from sqlalchemy.orm import Session

import models, schemas

def create_tracked(
        db: Session,
        tracked: schemas.TrackedCreate
):
    
    t = datetime.datetime.now()
    date = datetime.datetime(t.year, t.month, t.day)
    date_epoch = calendar.timegm(date.timetuple())

    db_tracked = models.Tracked(
        name=tracked.name,
        sites=tracked.sites,
        date_added=date_epoch,
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


def delete(
        db: Session,
        id: int
):
    db.query(models.Tracked) \
            .filter(models.Tracked.id == id) \
            .delete()
    db.commit()
