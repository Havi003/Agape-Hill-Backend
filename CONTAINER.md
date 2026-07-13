# Backend container workflow

The backend uses a multi-stage Java 17 image. Maven compiles the application in
the build stage, and only the executable Spring Boot JAR and Java runtime enter
the final image.

Environment values are supplied when the container starts. The `.dockerignore`
file prevents `.env` and `.env.production` from entering the build context.

## Local development

Keep PostgreSQL running on the development computer and make sure `.env`
contains the normal local database values. Compose replaces only `DB_HOST` with
`host.docker.internal`, because `localhost` inside the container refers to the
container itself.

Build and start the backend:

```bash
docker compose up --build
```

If port `8080` is already in use, choose another host port without changing
the port used by Spring inside the container:

```bash
HOST_PORT=18080 docker compose up --build
```

The API is available at `http://localhost:8080`, or at the `HOST_PORT` you
selected (for example, `http://localhost:18080`).

Stop it with:

```bash
docker compose down
```

## Production image

Render builds the same `Dockerfile` and supplies `PORT`, database credentials,
M-Pesa credentials, and `SPRING_PROFILES_ACTIVE=production` at runtime. Do not
copy `.env.production` into the image or commit it to Git.
