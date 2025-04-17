
kubectl rollout restart deploy auth-service
sleep 30
kubectl rollout restart deploy product-service
sleep 30
kubectl rollout restart deploy scraper-service
sleep 30
kubectl rollout restart deploy ui
