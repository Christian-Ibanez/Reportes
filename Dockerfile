# Usamos una imagen ligera de Java 17
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo JAR compilado al contenedor
COPY target/Reportes-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto 8081
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]