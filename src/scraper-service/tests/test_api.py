import os
import requests
from unittest.mock import patch

BASE_URL = os.getenv("BASE_URL", "http://localhost:8001")

def test_root_endpoint_success():
    response = requests.get(f"{BASE_URL}/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hello World"}

# Test cases for get tracked endpoint
def test_get_tracked_endpoint_success():
    response = requests.get(f"{BASE_URL}/tracked")
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    for item in response.json():
        assert 'id' in item
        assert 'name' in item
        assert 'sites' in item
        assert 'date_added' in item
        assert 'last_scraped' in item
        assert 'is_active' in item
        assert isinstance(item['date_added'], int)
        assert isinstance(item['last_scraped'], int)
        assert isinstance(item['is_active'], bool)

def test_get_tracked_endpoint_failure():
    response = requests.get(f"{BASE_URL}/tracked/12340")
    assert response.status_code == 404

# Test cases for create tracked endpoint
def test_create_tracked_endpoint_success():
    response = requests.post(f"{BASE_URL}/tracked", json={'name': 'cafe', 'sites': 'COTO'})
    assert response.status_code == 200
    assert isinstance(response.json(), dict)
    assert 'id' in response.json()
    assert 'name' in response.json()
    assert 'sites' in response.json()
    assert 'date_added' in response.json()
    assert 'last_scraped' in response.json()
    assert 'is_active' in response.json()
    assert isinstance(response.json()['date_added'], int)
    assert isinstance(response.json()['last_scraped'], int)
    assert isinstance(response.json()['is_active'], bool)

def test_create_tracked_endpoint_duplicate():
    response = requests.post(f"{BASE_URL}/tracked", json={'name': 'cafe1', 'sites': 'COTO'})
    assert response.status_code == 200
    response = requests.post(f"{BASE_URL}/tracked", json={'name': 'cafe1', 'sites': 'COTO'})
    assert response.status_code == 409
    assert response.json() == {'detail': 'Name already exists'}

def test_create_tracked_endpoint_invalid_name():
    response = requests.post(f"{BASE_URL}/tracked", json={'name': '', 'sites': 'COTO'})
    assert response.status_code == 422
    assert response.json() == {'detail': 'Name cannot be null'}

# Test toggle endpoint
def test_toggle_endpoint():
    response = requests.put(f"{BASE_URL}/toggle/1")
    assert response.status_code == 200
    assert response.json() == {"is_active": False}
    response = requests.put(f"{BASE_URL}/toggle/1")
    assert response.status_code == 200
    assert response.json() == {"is_active": True}

def test_toggle_endpoint_not_found():
    response = requests.put(f"{BASE_URL}/toggle/999")
    assert response.status_code == 404
    assert response.json() == {"detail": "Tracked item not found"}

# Test run_scraper_all endpoint success
def test_run_scraper_all_endpoint_success():
    with patch('scraper.Scraper.run') as mock_run:
        mock_run.return_value = None
        response = requests.post(f"{BASE_URL}/run_scraper")
    assert response.json() == {'message': 'Scraping all tracked items completed successfully.'}
    assert response.status_code == 200

def test_run_scraper_all_endpoint_no_new_data():
    with patch('scraper.Scraper.run') as mock_run:
        mock_run.return_value = None
        response = requests.post(f"{BASE_URL}/run_scraper")
        assert response.json() == {'message': 'No new data available for scraping today'}