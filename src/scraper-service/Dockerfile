###############
# Build Stage #
###############
FROM python:3.10-alpine AS builder

# Set working directory for installing dependencies.
WORKDIR /install

# Install system dependencies required during build time.
RUN apk update && apk add --no-cache \
    curl \
    unzip \
    wget \
    gnupg \
    chromium \
    chromium-chromedriver

# Set Chrome-related environment variables.
ENV CHROME_BIN=/usr/bin/chromium-browser \
    CHROME_DRIVER=/usr/bin/chromedriver

# Copy dependency definitions.
COPY requirements.txt .

# Install Python packages to a temporary location.
RUN pip install --no-cache-dir --prefix=/install -r requirements.txt


#################
# Runtime Stage #
#################
FROM python:3.10-alpine

# Set working directory for the application.
WORKDIR /code

# Set production environment variables.
ENV SCRAPER_DB_HOST=scraper-db \
    PRODUCT_SERVICE_HOST=product-service \
    HOST=0.0.0.0 \
    PORT=8001 \
    ENV=production

# Install runtime system dependencies.
RUN apk update && apk add --no-cache \
    curl \
    unzip \
    wget \
    gnupg \
    chromium \
    chromium-chromedriver

# Set Chrome-related environment variables.
ENV CHROME_BIN=/usr/bin/chromium-browser \
    CHROME_DRIVER=/usr/bin/chromedriver

# Create a non-root user to run the application.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the installed Python packages from the build stage.
COPY --from=builder /install /usr/local

# Copy the rest of the application code.
COPY . .

# Switch to a non-root user.
USER appuser

# Expose the application port.
EXPOSE 8001

# Use Uvicorn to run.
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001", "--proxy-headers"]
