# Stage 0: Copy and generate open-api stubs
FROM openapitools/openapi-generator-cli:v7.17.0 as api
WORKDIR /app
COPY api.yaml ./
RUN bash /usr/local/bin/docker-entrypoint.sh generate -i api.yaml -o src/generated -g typescript-fetch

# Stage 1: Build the Vite application
FROM node:18 as builder
# Set the working directory inside the container
WORKDIR /app
# Copy package.json and package-lock.json to install dependencies
COPY package.json package-lock.json ./
# Install dependencies - clean install
RUN npm ci 
# Copy all other application files into the working directory
COPY . .
## run open-api generator
#RUN npm run openapi-gen
COPY --from=api /app/src/generated ./src/generated
# Take vite mode as argument, default to production
ARG VITE_BUILD_MODE=production
# Build the Vite application for production
RUN npm run build -- --mode=$VITE_BUILD_MODE

# Stage 2: Serve with Nginx
FROM nginx:1.29-alpine
# add low privilege user
RUN chown -R nginx:nginx /var/cache/nginx /var/run
USER nginx
# Copy to templates so env variables can be injected
COPY nginx.conf /etc/nginx/templates/nginx.conf.template
# Copy the built files from the previous stage
COPY --from=builder /app/dist /usr/share/nginx/html
# Expose port 3000
EXPOSE 3000
# Start Nginx when the container runs
CMD ["nginx", "-g", "daemon off;"]
