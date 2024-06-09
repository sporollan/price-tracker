from sqlalchemy import Column, String, Boolean, BigInteger

from database import Base

class Tracked(Base):
    __tablename__ = "tracked"

    name = Column(String, primary_key=True)
    sites = Column(String)
    date_added = Column(BigInteger)
    is_active = Column(Boolean, default=True)
