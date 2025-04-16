./util/build-images.sh
sleep 2
MANIFEST_DIR="k8s/overlays/production"
if [[ -d "$MANIFEST_DIR" ]]; then
    kubectl apply -k "$MANIFEST_DIR"

    if [[ $? -ne 0 ]]; then
        echo "Failed to apply Kubernetes manifests."
        exit 1
    fi

    echo "Kubernetes manifests applied successfully."
else
    echo "Error: Manifest directory '$MANIFEST_DIR' does not exist."
    exit 1
fi
sleep 2
./util/restart.sh