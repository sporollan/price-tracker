import os
from unittest.mock import patch, MagicMock, AsyncMock
from fastapi.testclient import TestClient
from main import app

client = TestClient(app)
def test_root_endpoint_success():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hello World"}

def test_get_tracked_endpoint_success():
    response = client.get("/tracked", params={
        'user': 'u@mail.com',
        'skip': 0,
        'limit': 100})
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    for item in response.json():
        assert 'id' in item
        assert 'name' in item
        assert 'sites' in item
        assert 'date_added' in item
        assert 'last_scraped' in item
        assert isinstance(item['date_added'], int)
        assert isinstance(item['last_scraped'], int)

def test_get_tracked_endpoint_failure():
    response = client.get("/tracked/12340")
    assert response.status_code == 404

def test_create_tracked_endpoint_success():
    with patch('main.scrape_item', new_callable=AsyncMock) as mock_scrape_item:
        mock_scrape_item.return_value = True  # or True depending on what you want to test
        response = client.post("/tracked", json={'name': 'cafe', 'sites': 'COTO', 'users': ['u@gmail.com']})    
    assert response.status_code == 200
    assert isinstance(response.json(), dict)
    assert 'id' in response.json()
    assert 'name' in response.json()
    assert 'sites' in response.json()
    assert 'date_added' in response.json()
    assert 'last_scraped' in response.json()
    assert isinstance(response.json()['date_added'], int)
    assert isinstance(response.json()['last_scraped'], int)

def test_create_tracked_endpoint_duplicate():
    with patch('main.scrape_item', new_callable=AsyncMock) as mock_scrape_item:
        mock_scrape_item.return_value = False
        response = client.post("/tracked", json={'name': 'cafe1', 'sites': 'COTO', 'users': ['u@gmail.com']})
        assert response.status_code == 200
        response = client.post("/tracked", json={'name': 'cafe1', 'sites': 'COTO', 'users': ['u@gmail.com']})
        assert response.status_code == 409
        assert response.json() == {'detail': 'Name already exists'}

def test_create_tracked_endpoint_invalid_name():
    with patch('main.scrape_item', new_callable=AsyncMock) as mock_scrape_item:
        mock_scrape_item.return_value = False
        response = client.post("/tracked", json={'name': '', 'sites': 'COTO', 'users': ['u@gmail.com']})
        assert response.status_code == 422
        assert response.json() == {'detail': 'Name cannot be null'}

def test_toggle_endpoint():
    response = client.put("/toggle", params={'user': 'u@gmail.com', 'id': 1})
    assert response.status_code == 200
    assert response.json() == {"is_active": False}

def test_run_scraper_all_endpoint_no_new_data():
    # Mock get_tracked_all to return an empty list
    with patch('main.scrape_all', new_callable=AsyncMock) as mock_scrape_all:
        mock_scrape_all.return_value = None  # or True depending on what you want to test
        
        with patch('main.crud.get_tracked_all') as mock_get_all:
            mock_get_all.return_value = []  # Empty list means no tracked items
            
            response = client.post("/run_scraper")
            assert response.json() == {'detail': {'message': 'Scraping up to Date'}}
            assert response.status_code == 200

def test_run_scraper_all_endpoint_success():

    # Mock get_tracked_all to return a non-empty list
    with patch('main.crud.get_tracked_all') as mock_get_all:
        # Create a mock tracked item
        mock_tracked = MagicMock()
        mock_get_all.return_value = [mock_tracked]  # Non-empty list
        
        # Mock scrape_all as an AsyncMock since it's an async function
        with patch('main.scrape_all', new_callable=AsyncMock) as mock_scrape_all:
            mock_scrape_all.return_value = None  # or True depending on what you want to test
            
            response = client.post("/run_scraper")
            
    assert response.json() == {'detail': {'message': 'Scraping Started Successfully'}}
    assert response.status_code == 200