#!/usr/bin/env bash
# Compila todos os .java para a pasta out/
set -e
cd "$(dirname "$0")/.."
mkdir -p out
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
echo "Compilado com sucesso em out/"
