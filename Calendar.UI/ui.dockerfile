FROM node:20-slim AS base
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
RUN corepack enable
COPY . /app
WORKDIR /app

FROM base
RUN pnpm install --frozen-lockfile
RUN pnpm run build

FROM nginx:1.21.0-alpine as production
ENV NODE_ENV production
COPY --from=base /app/dist /usr/share/nginx/html

COPY nginx.conf ./nginx.conf
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]