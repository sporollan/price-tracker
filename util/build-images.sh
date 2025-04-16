# Build images on minikube docker environment
eval $(minikube -p price-tracker docker-env)
docker build -t product-service:dev ./src/product-service/
docker build -t scraper-service:dev ./src/scraper-service/
docker build -t ui:dev ./src/ui/
docker build -t auth-service:dev ./src/auth-service/
