# Security Analysis Tools

Этот проект настроен для автоматического анализа безопасности с помощью SpotBugs и OWASP Dependency-Check.

## Автоматические проверки

### GitHub Actions
Workflow автоматически запускается при:
- Push в ветки `main`, `master`, `develop`
- Создании Pull Request
- По расписанию каждый понедельник в 2:00 UTC

### Результаты анализа
- Результаты доступны во вкладке **Security** → **Code scanning alerts**
- Отчеты сохраняются как артефакты сборки (30 дней)
- Для Pull Request создаются автоматические комментарии с результатами

## Локальный запуск

### SpotBugs анализ
```bash
# Запуск SpotBugs анализа
mvn spotbugs:spotbugs

# Просмотр отчета
mvn spotbugs:gui

# Проверка результатов
cat target/spotbugsXml.xml
```

### OWASP Dependency Check
```bash
# Проверка зависимостей на уязвимости
mvn org.owasp:dependency-check-maven:check

# Результаты будут в target/dependency-check-report.html
```

## Настройка исключений

### SpotBugs исключения
Редактируйте файл `spotbugs-security-exclude.xml` для исключения ложных срабатываний:

```xml
<Match>
    <Class name="com.example.MyClass"/>
    <Bug pattern="SPECIFIC_BUG_PATTERN"/>
</Match>
```

### OWASP исключения
Редактируйте файл `owasp-suppressions.xml` для подавления известных уязвимостей:

```xml
<suppress>
    <notes>Объяснение почему это безопасно</notes>
    <packageUrl regex="true">^pkg:maven/group/artifact@.*$</packageUrl>
    <cve>CVE-XXXX-XXXX</cve>
</suppress>
```

## Пороги безопасности

- **SpotBugs**: Максимальное усилие, низкий порог (находит больше потенциальных проблем)
- **OWASP**: Сборка падает при обнаружении уязвимостей с CVSS ≥ 7
- **Dependency Review**: Блокирует зависимости с уровнем "moderate" и выше

## Рекомендации

1. Регулярно проверяйте отчеты безопасности
2. Обновляйте зависимости при обнаружении уязвимостей
3. Документируйте исключения в файлах конфигурации
4. Интегрируйте проверки в процесс разработки
