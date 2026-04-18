#基础镜像：使用OpenJDK17 (-slim比完整版小很多)
FROM openjdk:17-jdk-slim
#容器内的工作目录设置为/app 后续命令都在这个目录下执行
WORKDIR /app
#复制jar包到容器
#mvn package 命令会把项目打包成 jar 包，并放到 target/ 目录下。
COPY target/*.jar app.jar
#暴露端口
EXPOSE 8080
#启动命令
ENTRYPOINT ["jave","-jar","app.jar"]
