# 1. Usar una imagen base oficial de Java 17 (ligera)
FROM eclipse-temurin:17-jdk-alpine

# 2. Crear una carpeta interna llamada /app donde vivirá tu código
WORKDIR /app

# 3. Copiar el archivo .jar compilado desde tu carpeta target hacia el contenedor
# (El asterisco asegura que tome el archivo sin importar si la versión cambia)
COPY target/*.jar app.jar

# 4. Exponer el puerto 8081 al mundo exterior
EXPOSE 8081

# 5. El comando que se ejecutará al encender el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]