# FRONTEND BUILDER STAGE

FROM node:20-slim AS FRONTEND
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
RUN corepack enable
WORKDIR /build/
COPY ./ ./

RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --frozen-lockfile
RUN pnpm run build

# BACKEND BUILDER STAGE

FROM python:3.11 AS BACKEND

RUN pip install poetry

COPY poetry.lock pyproject.toml ./

RUN poetry export > ./requirements.txt

# PRODUCTION STAGE

FROM python:3.11-alpine AS PRODUCTION

# Install required packages for backend
COPY --from=BACKEND ./requirements.txt ./requirements.txt
RUN pip install -r requirements.txt
RUN rm ./requirements.txt

WORKDIR /run/

# Copy backend code
COPY ./src/backend/ ./

# Copy built frontend code
COPY --from=FRONTEND /build/dist/ ./static


EXPOSE 8080
CMD ["gunicorn", "--workers", "3", "--bind", "0.0.0.0:8080", "wsgi:app"]