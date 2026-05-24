#!/bin/bash
# Сборка проекта вручную (без Maven)
set -e

echo "=== Компиляция исходников ==="
mkdir -p build/classes
find src -name "*.java" > sources.txt
javac --release 17 -cp lib/sqlite-jdbc.jar -d build/classes @sources.txt

echo "=== Упаковка JAR ==="
cd build/classes
jar xf ../../lib/sqlite-jdbc.jar 2>/dev/null || true
cd ../..
echo "Main-Class: library.Main" > manifest.txt
jar cfm library-system.jar manifest.txt -C build/classes .

echo "=== Готово! Запуск: ==="
echo "  java -jar library-system.jar"
