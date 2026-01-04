FROM temurin:21-jdk-slim

WORKDIR /app

# Создаем non-root пользователя
RUN useradd -m -u 1001 appuser
USER appuser

# Копируем JAR файл
COPY --chown=appuser:appuser build/libs/*.jar app.jar

# Создаем директорию для логов
RUN mkdir -p logs

# Экспортируем порт (если нужно)
# EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]