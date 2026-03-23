#!/bin/bash

echo "=== Dev Container Post Start ==="

# Wait for MySQL to be ready
echo "Waiting for MySQL..."
until mysqladmin ping -h mysql -u root -prootpass --silent 2>/dev/null; do
    sleep 1
done
echo "✓ MySQL is ready"

# Wait for Redis to be ready
echo "Waiting for Redis..."
until redis-cli -h redis ping 2>/dev/null | grep -q "PONG"; do
    sleep 1
done
echo "✓ Redis is ready"

echo ""
echo "=== Services Status ==="
echo "MySQL: mysql -h mysql -u devuser -pdevpass innovation_platform"
echo "Redis: redis-cli -h redis"
echo ""
echo "=== Ready for development ==="
