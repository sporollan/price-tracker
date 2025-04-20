
kubectl rollout restart deploy auth-service
sleep 130 
kubectl rollout restart deploy product-service
sleep 130
kubectl rollout restart deploy scraper-service
sleep 130
kubectl rollout restart deploy ui
