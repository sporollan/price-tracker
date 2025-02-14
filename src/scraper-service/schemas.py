from pydantic import BaseModel, ConfigDict

class TrackedBase(BaseModel):
    name: str
    sites: str

class Tracked(TrackedBase):
    id: int
    date_added: int
    last_scraped: int
    is_active: bool
    model_config = ConfigDict(
        from_attributes=True)

class TrackedCreate(TrackedBase):
    pass