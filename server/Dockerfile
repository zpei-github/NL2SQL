FROM eclipse-temurin:8-jre

RUN mkdir -p /multillm-mysql
WORKDIR /multillm-mysql

COPY ./target/multiLLM-0.0.1-SNAPSHOT.jar app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"
ENV ARGS=""

EXPOSE 18888

CMD java ${JAVA_OPTS} -jar app.jar $ARGS