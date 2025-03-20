from sqlalchemy import Column, ForeignKey, Integer, String, Boolean, BigInteger, Table
from sqlalchemy.orm import relationship
from database import Base


user_tracked_association = Table(
    'user_tracked_association', Base.metadata,
    Column('user_id', String, ForeignKey('user.id'), primary_key=True),
    Column('tracked_id', Integer, ForeignKey('tracked.id'), primary_key=True)
)


class Tracked(Base):
    __tablename__ = "tracked"
    id = Column(Integer, primary_key=True)

    name = Column(String)
    sites = Column(String)
    date_added = Column(BigInteger)
    last_scraped = Column(BigInteger)

    users = relationship("User", secondary=user_tracked_association, back_populates="tracked_items")


class User(Base):
    __tablename__ = "user"
    id = Column(String, primary_key=True)
    tracked_items = relationship("Tracked", secondary=user_tracked_association, back_populates="users")


