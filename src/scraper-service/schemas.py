from typing import List, Optional
from pydantic import BaseModel, ConfigDict, validator

class TrackedBase(BaseModel):
    name: str
    sites: str

class Tracked(TrackedBase):
    id: int
    date_added: int
    last_scraped: int
    model_config = ConfigDict(
        from_attributes=True)

class TrackedCreate(TrackedBase):
    # Make this optional since it might not be passed in API calls
    users: Optional[List[str]] = None

    @classmethod
    def validate(cls, values):
        if not values.get("users"):
            raise ValueError("At least one user must be provided.")
        return values