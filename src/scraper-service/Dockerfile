FROM python:3.10-alpine
WORKDIR /code
ENV SCRAPER_DB_HOST=scraper-db
ENV PRODUCT_SERVICE_HOST=product-service
ENV HOST=0.0.0.0
ENV PORT=8001

# Install system dependencies
RUN apk update && apk add --no-cache \
    curl \
    unzip \
    wget \
    gnupg \
    chromium \
    chromium-chromedriver

# Set up Chrome and ChromeDriver
ENV CHROME_BIN=/usr/bin/chromium-browser \
    CHROME_DRIVER=/usr/bin/chromedriver

# Install Python dependencies
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt

EXPOSE 8001
COPY . .
CMD sleep 6 && fastapi run main.py --proxy-headers --port 8001
