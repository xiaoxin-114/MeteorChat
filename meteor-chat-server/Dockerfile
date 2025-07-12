# 使用基础镜像（OpenJDK）
FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制 JAR 文件到容器中
COPY meteor-chat-server-1.0-SNAPSHOT.jar meteor-chat-server-1.0-SNAPSHOT.jar

# 暴露应用端口（默认是 8080）
EXPOSE 8000

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "meteor-chat-server-1.0-SNAPSHOT.jar"]
