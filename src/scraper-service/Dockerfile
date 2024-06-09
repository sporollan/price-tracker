FROM python:3.10-alpine
WORKDIR /code
ENV SCRAPER_DB_HOST=scraper_db
ENV HOST=0.0.0.0
ENV PORT=8001
RUN apk add --no-cache gcc musl-dev linux-headers
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
EXPOSE 8001
COPY . .
CMD sleep 6 && fastapi run main.py --proxy-headers --port 8001
