FROM node
WORKDIR /code
COPY package.json /code/package.json
COPY package-lock.json /code/package-lock.json

RUN npm install 
COPY . /code

ENV CI=true
ENV PORT=3000

CMD [ "npm", "start" ]
