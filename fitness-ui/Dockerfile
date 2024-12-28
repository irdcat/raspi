FROM node:20 AS build

WORKDIR /fitness
RUN groupadd fitness
RUN useradd -g fitness fitness
USER fitness:fitness
RUN mkdir -p ~/.npm-global/lib
RUN npm config set prefix '~/.npm-global'
RUN export PATH=~/.npm-global/bin:$PATH
RUN source ~/.profile
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html