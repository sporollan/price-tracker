from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_root_endpoint_success():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hello World"}

# test get tracked endpoint
def test_get_tracked_endpoint_success():
    response = client.get("/tracked")
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    for item in response.json():
        assert "id" in item
        assert "name" in item
        assert "sites" in item
        assert "date_added" in item
        assert "last_scraped" in item
        assert "is_active" in item
        assert isinstance(item["date_added"], int)
        assert isinstance(item["last_scraped"], int)
        assert isinstance(item["is_active"], bool)


def test_get_tracked_endpoint_failure():
    response = client.get("/tracked/9123")
    assert response.status_code == 404

# Test cases for create_tracked endpoint
def test_create_tracked_endpoint_success():
    response = client.post("/tracked", json={"name": "cafe", "sites":"COTO"})
    assert response.status_code == 200
    assert isinstance(response.json(), dict)
    assert "id" in response.json()
    assert "name" in response.json()
    assert "sites" in response.json()
    assert "date_added" in response.json()
    assert "last_scraped" in response.json()
    assert "is_active" in response.json()
    assert isinstance(response.json()["date_added"], int)
    assert isinstance(response.json()["last_scraped"], int)
    assert isinstance(response.json()["is_active"], bool)

def test_create_tracked_endpoint_duplicate():
    response = client.post("/tracked", json={"name": "cafe1", "sites":"COTO"})
    assert response.status_code == 200
    response = client.post("/tracked", json={"name": "cafe1", "sites":"COTO"})
    assert response.status_code == 409
    assert response.json() == {"detail": "Name already exists"}

def test_create_tracked_endpoint_invalid_name():
    response = client.post("/tracked", json={"name": "", "sites":"COTO"})
    assert response.status_code == 422
    assert response.json() == {"detail": "Name cannot be null"}
