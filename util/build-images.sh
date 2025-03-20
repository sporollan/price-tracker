eval export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://192.168.49.2:2376"
export DOCKER_CERT_PATH="/home/sporollan/.minikube/certs"
export MINIKUBE_ACTIVE_DOCKERD="minikube"

# To point your shell to minikube's docker-daemon, run:
eval $(minikube -p price-tracker docker-env)
docker build -t product-service:dev ./src/product-service/
docker build -t scraper-service:dev ./src/scraper-service/
docker build -t ui:dev ./src/ui/
docker build -t auth-service:dev ./src/auth-service/
