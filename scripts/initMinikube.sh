#!/bin/bash
set -e  # Exit on error

# Source loading animations functions and animations
source ./bash_loading_animations.sh

# Function to safely stop animation and handle errors
cleanup() {
    if [ -n "${BLA_loading_animation_pid:-}" ] && kill -0 "${BLA_loading_animation_pid}" 2>/dev/null; then
        BLA::stop_loading_animation
    fi
}

# Function to run command with animation and error handling
run_with_animation() {
    local message="$1"
    local animation_type="${2:-filling_bar}"
    shift 2

    echo "$message"

    # Use different animation types for variety
    case "$animation_type" in
        "spinning")
            # shellcheck disable=SC2154
            BLA::start_loading_animation "${BLA_spinning_plus[@]}"
            ;;
        "dots")
            # shellcheck disable=SC2154
            BLA::start_loading_animation "${BLA_moving_dots[@]}"
            ;;
        "bar")
            # shellcheck disable=SC2154
            BLA::start_loading_animation "${BLA_filling_bar[@]}"
            ;;
        *)
            BLA::start_loading_animation "${BLA_filling_bar[@]}"
            ;;
    esac

    # Run the command and capture exit code
    if "$@"; then
        BLA::stop_loading_animation
        echo "✅ Done!"
    else
        BLA::stop_loading_animation
        echo "❌ Failed!"
        exit 1
    fi
}

# Ensure we stop animation cleanly if script is interrupted
trap cleanup SIGINT SIGTERM EXIT

cd ../
run_with_animation "🚀 Starting Minikube..." "spinning" minikube start

echo "⚓ Setting up Minikube to use docker-env"
eval "$(minikube docker-env)"
echo "✅ Docker environment configured!"

run_with_animation "🛠️ Building Maven project..." "dots" mvn clean package -DskipTests

run_with_animation "🐳 Building Docker image for auth-service..." "bar" docker build --no-cache -t auth-service:local .

echo "📦 Loading auth-service image into Minikube..."
minikube image load auth-service:local
echo "✅ Image loaded successfully!"

echo "📁 Setting up persistent volumes in Minikube..."
echo "🔹 Creating and setting permissions for /mnt/postgres-data..."
minikube ssh 'sudo mkdir -p /mnt/postgres-data && sudo chown 1001:1001 /mnt/postgres-data && sudo chmod 755 /mnt/postgres-data'

echo "🔹 Creating and setting permissions for /mnt/rabbitmq-data..."
minikube ssh 'sudo mkdir -p /mnt/rabbitmq-data && sudo chown 1001:1001 /mnt/rabbitmq-data && sudo chmod 755 /mnt/rabbitmq-data'

echo "🔹 Creating and setting permissions for /data/pgadmin..."
minikube ssh 'sudo mkdir -p /data/pgadmin && sudo chown 5050:5050 /data/pgadmin && sudo chmod 755 /data/pgadmin'

echo "📄 Applying Kubernetes manifests for RabbitMQ..."
for file in k8s/helm/rabbitmq/*.yml; do
  [[ "$file" == *values*.yml ]] && continue
  kubectl apply -f "$file"
done
echo "✅ RabbitMQ manifests applied!"

echo "📄 Applying Kubernetes manifests for PostgreSQL..."
for file in k8s/helm/postgres/*.yml; do
  [[ "$file" == *values*.yml ]] && continue
  kubectl apply -f "$file"
done
echo "✅ PostgreSQL manifests applied!"

echo "📄 Applying Kubernetes manifests for PgAdmin..."
for file in k8s/helm/pgadmin/*.yml; do
  [[ "$file" == *values*.yml ]] && continue
  kubectl apply -f "$file"
done
echo "✅ PgAdmin manifests applied!"

run_with_animation "🚀 Installing PostgreSQL Helm chart..." "spinning" helm install k8s-formiit-postgres bitnami/postgresql -f k8s/helm/postgres/values.yml

run_with_animation "⏳ Waiting for PostgreSQL pod to be ready..." "dots" kubectl wait --namespace default --for=condition=Ready pod -l app.kubernetes.io/name=postgresql --timeout=300s

run_with_animation "🚀 Installing RabbitMQ Helm chart..." "spinning" helm install k8s-formiit-rabbitmq bitnami/rabbitmq -f k8s/helm/rabbitmq/values.yml

run_with_animation "⏳ Waiting for RabbitMQ pod to be ready..." "dots" kubectl wait --namespace default --for=condition=Ready pod -l app.kubernetes.io/name=rabbitmq --timeout=300s

# Check if PgAdmin release already exists and handle accordingly
echo "🔍 Checking if PgAdmin Helm release exists..."
if helm list -q | grep -q "k8s-formiit-pgadmin"; then
    echo "⚠️  PgAdmin release already exists. Upgrading instead..."
    run_with_animation "🔄 Upgrading PgAdmin Helm chart..." "spinning" helm upgrade k8s-formiit-pgadmin runix/pgadmin4 -f k8s/helm/pgadmin/values.yml
else
    run_with_animation "🚀 Installing PgAdmin Helm chart..." "spinning" helm install k8s-formiit-pgadmin runix/pgadmin4 -f k8s/helm/pgadmin/values.yml
fi

run_with_animation "⏳ Waiting for PgAdmin pod to be ready..." "dots" kubectl wait --namespace default --for=condition=Ready pod -l app.kubernetes.io/name=pgadmin4 --timeout=300s

echo "📄 Applying auth-service manifests (config map, service, secrets, deployment)..."
kubectl apply -f k8s/auth-service
echo "✅ Auth-service manifests applied!"

run_with_animation "⏳ Waiting for auth-service deployment to be ready..." "bar" kubectl rollout status deployment auth-service --timeout=300s

echo "🎉 All services deployed and ready!"