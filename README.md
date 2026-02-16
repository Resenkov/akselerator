# Akselerator — Лабораторная работа №2 (Docker Compose)

Проект подготовлен под требования лабораторной работы:

- backend (Spring Boot);
- сервер базы данных PostgreSQL (отдельный контейнер);
- proxy-сервер (Nginx + статический frontend), через который идет весь внешний доступ.

## Состав контейнеров

Система запускается через `docker-compose.yml` и включает:

1. `acselerator-app` — backend-сервис (Spring Boot, порт 8080 только во внутренней сети);
2. `acselerator-db` — PostgreSQL (порт 5432 только во внутренней сети);
3. `acselerator-proxy` — Nginx (единственная внешняя точка входа, `localhost:3000`).

## Сетевое взаимодействие

- `internal_net` (internal: true): изолированная сеть для взаимодействия `backend <-> db <-> proxy`.
- `edge_net`: внешняя сеть для публикации только proxy-контейнера.

`acselerator-app` и `acselerator-db` **не имеют опубликованных наружу портов** и недоступны извне.

## Запуск

```bash
docker compose up --build
```

После запуска сайт доступен по адресу:

- `http://localhost:3000`

API-запросы из frontend идут на `/api` и проксируются Nginx в backend-контейнер.

## Остановка

```bash
docker compose down
```

Для удаления тома БД:

```bash
docker compose down -v
```
