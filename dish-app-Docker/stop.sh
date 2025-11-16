#!/bin/bash

echo "🛑 Parando Sistema de Comanda Digital..."
echo "=========================================="

# Parar todos os containers
docker-compose down

echo "✅ Sistema parado com sucesso!"
echo "Para remover volumes e dados, execute: docker-compose down -v"

