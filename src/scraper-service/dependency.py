
import database
import calendar
import datetime

from scraper import Scraper

# Dependencies
def get_db():
    db = database.SessionLocal()
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