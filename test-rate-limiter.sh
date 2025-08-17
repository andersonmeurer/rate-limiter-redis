#!/bin/bash
echo "=== Testando Rate Limiter ==="

echo "1. Fazendo 10 requisições rápidas (devem ser aceitas)..."
for i in {1..10}; do
  response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/test)
  echo "Requisição $i: HTTP $response"
done

echo -e "\n2. Fazendo 11ª requisição (deve ser rejeitada)..."
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/test)
echo "Requisição 11: HTTP $response"

if [ "$response" = "429" ]; then
  echo "✅ Rate limiter funcionando corretamente!"
else
  echo "❌ Rate limiter não está funcionando como esperado."
fi

echo -e "\n3. Aguardando reset da janela (6 segundos)..."
sleep 6

echo "4. Testando após reset..."
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/test)
echo "Requisição após reset: HTTP $response"

if [ "$response" = "200" ]; then
  echo "✅ Reset da janela funcionando corretamente!"
else
  echo "❌ Reset da janela não está funcionando como esperado."
fi

echo -e "\n=== Teste completo! ==="