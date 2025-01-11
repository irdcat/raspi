FROM node:20 AS build

USER node
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global
ENV PATH=$PATH:/home/node/.npm-global/bin
WORKDIR /home/node
COPY package.json .
RUN npm install --only=prod
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /home/node/build /usr/share/nginx/html
# RUN cp /etc/nginx/conf.d/default.conf /nginx-default.conf.bak
COPY nginx.conf /etc/nginx/conf.d/default.conf