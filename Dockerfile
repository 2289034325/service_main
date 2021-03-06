FROM registry.cn-shanghai.aliyuncs.com/acxca/openjdk8-ex:latest

ENV LANG en_US.UTF-8
RUN apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*

WORKDIR /home
COPY *.jar app.jar
COPY *.properties ./
COPY start.sh start.sh
RUN chmod +x start.sh

EXPOSE 80

ENTRYPOINT ["/home/start.sh"]