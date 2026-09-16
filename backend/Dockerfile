# Production Multi-Stage Dockerfile for Housie Shopping API
FROM node:20-alpine AS dependencies
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm ci --only=production

FROM node:20-alpine AS runner
WORKDIR /usr/src/app
ENV NODE_ENV=production

# Security: Run as unprivileged node user
USER node

# Copy dependencies and application code
COPY --chown=node:node --from=dependencies /usr/src/app/node_modules ./node_modules
COPY --chown=node:node package*.json ./
COPY --chown=node:node src ./src

EXPOSE 5000
EXPOSE 10000

CMD ["node", "src/server.js"]
