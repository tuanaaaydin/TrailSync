#!/bin/bash
echo "=== TrailSync Build ==="

mkdir -p shared/out server/out client/out gui/out

echo "[1/4] shared..."
javac -d shared/out shared/src/main/java/trailsync/shared/*.java || exit 1

echo "[2/4] server..."
javac -cp shared/out -d server/out server/src/main/java/trailsync/server/*.java || exit 1

echo "[3/4] client..."
javac -cp shared/out -d client/out client/src/main/java/trailsync/client/*.java || exit 1

echo "[4/4] gui..."
javac -cp shared/out -d gui/out gui/src/main/java/trailsync/gui/*.java || exit 1

echo "✅ Build fertig!"
