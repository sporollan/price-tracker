from sqlalchemy import Column, Integer, String, Boolean, BigInteger

from database import Base

class Tracked(Base):
    __tablename__ = "tracked"
    id = Column(Integer, primary_key=True)

    name = Column(String)
    sites = Column(String)
    date_added = Column(BigInteger)
    is_active = Column(Boolean, default=True)
