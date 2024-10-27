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
# Build the Vite application for production
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:latest
# Copy custom Nginx configuration file
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
# Copy the built files from the previous stage
COPY --from=builder /app/dist /usr/share/nginx/html
# Expose port 3000
EXPOSE 3000
# Start Nginx when the container runs
CMD ["nginx", "-g", "daemon off;"]
