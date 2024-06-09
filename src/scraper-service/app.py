from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware


app = FastAPI()
origins = [
    "http://localhost",
    "http://localhost:3000"
]
# Dependency
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

@app.post('/tracked')
def create_tracked():
    pass

@app.get('/tracked')
def get_all():
    pass

@app.get('/tracked/{id}')
def get_one():
    pass

@app.put('/tracked/{id}')
def update_tracked():
    pass

@app.delete('/tracked/{id}')
def delete_tracked():
    pass

@app.post('/run_scraper')
def run_scraper_all():
    pass

@app.post('/run_scraper/{id}')
def run_scraper_one():
    pass

