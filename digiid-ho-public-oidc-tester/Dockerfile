FROM node:16-alpine@sha256:425c81a04546a543da824e67c91d4a603af16fbc3d875ee2f276acf8ec2b1577
WORKDIR /app
COPY . .
RUN npm ci --ignore-scripts

EXPOSE 3030
CMD [ "node", "app.js" ]
