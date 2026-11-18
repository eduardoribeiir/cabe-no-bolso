#!/usr/bin/env bash
# Executa a aplicação (compile antes com ./scripts/compilar.sh)
cd "$(dirname "$0")/.."
java -cp out cabenobolso.Main
