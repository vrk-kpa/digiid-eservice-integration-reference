FROM node:16-alpine@sha256:425c81a04546a543da824e67c91d4a603af16fbc3d875ee2f276acf8ec2b1577 AS builder
WORKDIR /app
COPY . .
ARG ENVIRONMENT
RUN npm ci --ignore-scripts

# Disable collection of Next.js anonymous telemetry data.
# Learn more here: https://nextjs.org/telemetry
ENV NEXT_TELEMETRY_DISABLED 1

RUN npm run build-${ENVIRONMENT}

# Production image, copy all the files and run next
FROM node:16-alpine@sha256:425c81a04546a543da824e67c91d4a603af16fbc3d875ee2f276acf8ec2b1577 AS runner
WORKDIR /app
RUN addgroup -g 1001 -S nodejs
RUN adduser -S nextjs -u 1001

# You only need to copy next.config.js if you are NOT using the default configuration
COPY --from=builder /app/next.config.js ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/package.json ./package.json

# Automatically leverage output traces to reduce image size 
# https://nextjs.org/docs/advanced-features/output-file-tracing
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static
COPY --from=builder --chown=nextjs:nodejs /app/node_modules/next-logger ./node_modules/next-logger

USER nextjs
EXPOSE 3100
ENV PORT 3100
ENV NODE_ENV production
ENV NEXT_TELEMETRY_DISABLED 1

CMD ["node", "server.js"]
