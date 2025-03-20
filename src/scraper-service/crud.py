
from sqlalchemy.orm import joinedload
from sqlalchemy.orm import Session

import models, schemas

def create_tracked(
        db: Session,
        tracked: schemas.TrackedCreate,
        user_id: str,
        now: int
):

    user = db.query(models.User).filter(models.User.id == user_id).first()

    db_tracked = models.Tracked(
        name=tracked.name,
        sites=tracked.sites,
        date_added=now,
        last_scraped=0
    )
    user.tracked_items.append(db_tracked)
    db.add(db_tracked)
    db.commit()
    db.refresh(db_tracked)
    return db_tracked

def add_to_user(
        db: Session,
        tracked_id: int,
        user_id: str
):
    user = db.query(models.User).filter(models.User.id == user_id).first()
    tracked = db.query(models.Tracked).filter(models.Tracked.id == tracked_id).first()
    if tracked not in user.tracked_items:
        user.tracked_items.append(tracked)
        db.commit()
    return tracked

def remove_from_user(
        db: Session,
        tracked_id: int,
        user_id: str
):
    user = db.query(models.User).filter(models.User.id == user_id).first()
    tracked = db.query(models.Tracked).filter(models.Tracked.id == tracked_id).first()
    
    if tracked in user.tracked_items:
        user.tracked_items.remove(tracked)
        db.commit()
    
    return tracked

def get_one(
        db: Session,
        id: int
):
    return db.query(models.Tracked) \
            .filter(models.Tracked.id == id) \
            .first()

def get_all(
        db: Session,
        user: str,
        skip: int,
        limit: int
):
    user = db.query(models.User) \
             .options(joinedload(models.User.tracked_items)) \
             .filter(models.User.id == user) \
             .first()
    
    if user:
        return user.tracked_items
    else:
        return []  # Return empty list if user not found


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


def get_tracked_all(
        db: Session
):
    return db.query(models.Tracked) \
                .all()

def get_by_name(
        db: Session,
        name: str
):
    return db.query(models.Tracked) \
                .filter(models.Tracked.name == name) \
                .first()

def get_user_by_id(db: Session,
                user: str
):
    return db.query(models.User) \
                .filter(models.User.id == user) \
                .first()

def create_user(db: Session,
                user: str
):
        db_user = models.User(
                id=user
        )
        db.add(db_user)
        db.commit()
        db.refresh(db_user)
        return db_user


def get_one_tracked_by_id_and_user(db: Session,
                                   id: int,
                                   user_id: str):
    db.query(models.Tracked) \
             .options(joinedload(models.Tracked.users)) \
             .filter(models.Tracked.id == id)

    user = db.query(models.User) \
             .options(joinedload(models.User.tracked_items)) \
             .filter(models.User.id == user) \
    
    user.tracked_items
