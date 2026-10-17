# GromVisuals

Клиентский Fabric-мод с современным HUD и настраиваемым меню для Minecraft Java **1.21.11**.

## Что внутри

- Меню в тёмном стиле с анимацией появления, боковой навигацией и кликабельными переключателями.
- HUD-модули: GromVisuals-бейдж, FPS, пинг, координаты, название биома, клавиши WASD / Jump / Sneak и внутриигровое время.
- Четыре акцентные темы: Violet, Cyan, Amber и Mint.
- Компактный режим и возможность отключить анимации.
- Настройки сохраняются в `config/gromvisuals.json`.
- GitHub Actions автоматически собирает remapped `.jar`.

Мод клиентский: устанавливать его на сервер не нужно. Для игры требуется Fabric Loader и Fabric API соответствующей версии.

## Управление

- **Right Shift** — открыть меню.
- **Esc** или повторный **Right Shift** — закрыть меню.

## Как получить JAR через GitHub Actions

1. Создай пустой GitHub-репозиторий.
2. Распакуй ZIP и загрузи **содержимое папки `GromVisuals`** в корень репозитория. В корне должны лежать `build.gradle`, `settings.gradle`, `gradle.properties`, `src` и `.github`.
3. Сделай `Commit` в ветку `main` (или `master`).
4. Открой вкладку **Actions → Build GromVisuals** и дождись зелёной галочки.
5. Открой завершённый запуск и скачай артефакт **GromVisuals-1.0.0**. Внутри будет `GromVisuals-1.0.0.jar`.
6. Положи JAR в папку `.minecraft/mods`. В профиле должны быть Minecraft 1.21.11, Java 21, Fabric Loader и Fabric API 0.141.6+1.21.11.

Workflow запускается автоматически при push/pull request и вручную через `Run workflow`.

## Сборка локально (необязательно)

Нужны JDK 21 и Gradle 8.14.3. Из корня проекта выполни:

```bash
gradle --no-daemon build
```

Готовый remapped JAR появится в `build/libs/GromVisuals-1.0.0.jar`.

## Технические параметры

- Minecraft: `1.21.11`
- Java: `21`
- Fabric Loader: `0.18.4+`
- Fabric API: `0.141.6+1.21.11`
- Fabric Loom: `1.14.10`
- Mod ID: `gromvisuals`

## Важно

Это стартовая рабочая версия визуального клиента, а не копия закрытых/платных функций EnoughVisuals. Все элементы в проекте реализованы самостоятельно; HUD использует реальные данные Minecraft.
