FROM eclipse-temurin:21-jdk
WORKDIR /app

# Создаем non-root пользователя
RUN useradd -m -u 1001 appuser

# Создаем директорию для логов
RUN mkdir -p logs && \
    mkdir -p logs/archived && \
    chown -R appuser:appuser logs

USER appuser

# Копируем JAR файл
COPY --chown=appuser:appuser build/libs/*.jar app.jar

# Экспортируем порт (если нужно)
# EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]