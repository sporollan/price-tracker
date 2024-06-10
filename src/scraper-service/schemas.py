from pydantic import BaseModel

class TrackedBase(BaseModel):
    name: str
    sites: str

class Tracked(TrackedBase):
    id: int
    date_added: int
    last_scraped: int
    is_active: bool

    class Config:
        from_attributes = True

class TrackedCreate(TrackedBase):
    pass