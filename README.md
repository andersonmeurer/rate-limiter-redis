# Rate Limiter com Redis

Este projeto implementa um sistema de rate limiting (limitação de taxa) usando Spring Boot e Redis. O sistema limita o número de requisições por endereço IP em uma janela de tempo específica.

## 📋 Características

- **Limite de Requisições**: 10 requisições por IP
- **Janela de Tempo**: 5 segundos
- **Tecnologia**: Spring Boot 3.2.5 + Redis
- **Resposta para Limite Excedido**: HTTP 429 (Too Many Requests)

## 🔧 Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- **Java 17** ou superior
- **Maven 3.6+**
- **Docker** (para executar o Redis)
- **Git** (para clonar o repositório)

### Verificar Instalações

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar Docker
docker --version
```

## 🚀 Passo a Passo para Executar o Projeto

### 1. Clonar o Repositório

```bash
git clone https://github.com/andersonmeurer/rate-limiter-redis.git
cd rate-limiter-redis
```

### 2. Iniciar o Redis com Docker

O projeto inclui um arquivo `docker-compose.yml` configurado para o Redis:

```bash
# Iniciar o Redis em background (usar docker compose ou docker-compose)
docker compose up -d
# ou se você tem uma versão mais antiga do Docker:
# docker-compose up -d

# Verificar se o Redis está executando
docker compose ps
# ou: docker-compose ps
```

Você deve ver uma saída similar a:
```
      Name                    Command               State           Ports         
---------------------------------------------------------------------------------
redis-rate-limiter   docker-entrypoint.sh redis ...   Up      0.0.0.0:6379->6379/tcp
```

### 3. Compilar o Projeto

```bash
# Limpar e compilar o projeto
mvn clean compile
```

### 4. Executar os Testes

```bash
# Executar todos os testes
mvn test
```

Os testes verificam:
- ✅ Requisições dentro do limite são permitidas
- ✅ Requisições excedendo o limite são bloqueadas (HTTP 429)
- ✅ Limite é resetado após a janela de tempo expirar

### 5. Executar a Aplicação

```bash
# Iniciar a aplicação Spring Boot
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 🧪 Como Testar o Rate Limiter

### Teste Manual com curl

#### 1. Teste Básico - Requisições Permitidas
```bash
# Fazer 10 requisições (dentro do limite)
for i in {1..10}; do
  echo "Requisição $i:"
  curl -w "\nStatus: %{http_code}\n" http://localhost:8080/api/test
  echo "---"
done
```

#### 2. Teste de Limite Excedido
```bash
# 11ª requisição - deve retornar 429
echo "11ª Requisição (deve ser bloqueada):"
curl -w "\nStatus: %{http_code}\n" http://localhost:8080/api/test
```

#### 3. Teste de Reset da Janela
```bash
# Aguardar 6 segundos para reset da janela
echo "Aguardando reset da janela (6 segundos)..."
sleep 6

# Fazer nova requisição - deve ser permitida
echo "Requisição após reset:"
curl -w "\nStatus: %{http_code}\n" http://localhost:8080/api/test
```

### Teste com Script Automatizado

Crie um arquivo `test-rate-limiter.sh`:

```bash
#!/bin/bash
echo "=== Testando Rate Limiter ==="

echo "1. Fazendo 10 requisições (devem ser aceitas)..."
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
```

Executar o script:
```bash
chmod +x test-rate-limiter.sh
./test-rate-limiter.sh
```

## ⚙️ Configuração

### Configuração do Redis

As configurações do Redis estão em `src/main/resources/application.properties`:

```properties
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### Configuração do Rate Limiter

As configurações do rate limiter estão em `RateLimiterInterceptor.java`:

```java
public static final int MAX_REQUESTS = 10;      // Máximo de requisições
public static final int WINDOW_IN_SECONDS = 5;  // Janela de tempo em segundos
```

Para alterar os limites, modifique essas constantes e recompile o projeto.

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/meurer/ratelimiter/
│   │   ├── RateLimiterApplication.java          # Classe principal
│   │   ├── config/
│   │   │   ├── RateLimiterInterceptor.java      # Lógica do rate limiter
│   │   │   └── WebMvcConfig.java                # Configuração do interceptor
│   │   └── controller/
│   │       └── TestController.java              # Endpoint de teste
│   └── resources/
│       └── application.properties               # Configurações
└── test/
    └── java/com/meurer/ratelimiter/
        └── RateLimiterTest.java                 # Testes do rate limiter
```

## 🔍 Como Funciona

1. **Interceptor**: Cada requisição HTTP passa pelo `RateLimiterInterceptor`
2. **Chave Redis**: Usa o IP do cliente como chave: `rate-limiter:{IP}`
3. **Contador**: Incrementa um contador no Redis para cada requisição
4. **Expiração**: Define TTL de 5 segundos na primeira requisição
5. **Verificação**: Se o contador > 10, retorna HTTP 429
6. **Reset**: Após 5 segundos, a chave expira e o contador é resetado

## 🐛 Solução de Problemas

### Redis não está executando
```bash
# Verificar status do container
docker compose ps
# ou: docker-compose ps

# Ver logs do Redis
docker compose logs redis
# ou: docker-compose logs redis

# Reiniciar o Redis
docker compose restart redis
# ou: docker-compose restart redis
```

### Erro de conexão com Redis
- Verifique se a porta 6379 não está sendo usada por outro processo
- Confirme que o Docker está executando
- Verifique as configurações em `application.properties`

### Testes falhando
```bash
# Limpar dados do Redis
docker compose exec redis redis-cli FLUSHALL
# ou: docker-compose exec redis redis-cli FLUSHALL

# Executar testes novamente
mvn test
```

### Aplicação não inicia
```bash
# Verificar se a porta 8080 está livre
netstat -tulpn | grep 8080

# Executar em porta diferente
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## 🛑 Parar os Serviços

```bash
# Parar a aplicação Spring Boot
Ctrl + C (no terminal onde está executando)

# Parar o Redis
docker compose down
# ou: docker-compose down
```

## 📝 Logs

Para acompanhar os logs da aplicação:

```bash
# Logs do Redis
docker compose logs -f redis
# ou: docker-compose logs -f redis

# Logs da aplicação aparecem no console onde foi executado mvn spring-boot:run
```

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.