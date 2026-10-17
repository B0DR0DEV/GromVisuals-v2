# GromVisuals 1.0.1

Клиентский Fabric-мод с настраиваемым HUD и анимированным меню для Minecraft Java **1.21.11**.

## Что внутри

- Тёмное меню с анимацией появления, боковой навигацией и кликабельными настройками.
- HUD: название мода, FPS, пинг, координаты, биом, клавиши WASD / Jump / Sneak и внутриигровое время.
- Четыре цветовые темы: Violet, Cyan, Amber и Mint.
- Компактный режим и отключение анимаций.
- Сохранение настроек в `config/gromvisuals.json`.
- Сборка JAR через GitHub Actions.

## Управление

- **Right Shift** — открыть меню.
- **Esc** или повторный **Right Shift** — закрыть меню.

## Исправления сборки #45

- Используется API Fabric `KeyBindingHelper` из `net.fabricmc.fabric.api.client.keybinding.v1`.
- Создаётся категория биндов Minecraft 1.21.11 через `KeyMapping.Category.register(Identifier)`.
- GUI использует `MouseButtonEvent` и `KeyEvent`, как требует Minecraft 1.21.11.
- Для ключа биома используется `ResourceKey.identifier()`.
- Gradle 9.2.1 используется с Fabric Loom 1.14.10.

## Текущая структура репозитория GromVisuals-v2

Архив подготовлен для существующего расположения файлов: workflow лежит в корне репозитория, а Gradle-проект — в `GromVisuals-GitHubActions-FIXED2/`.

1. Распакуй ZIP.
2. Замени содержимое текущей папки `GromVisuals-GitHubActions-FIXED2/` новыми файлами из архива с тем же путём.
3. Замени файл `.github/workflows/build.yml` в корне репозитория версией из архива.
4. Проверь, что в репозитории остался **один актуальный Gradle-проект**, а не несколько папок `FIXED`, `FIXED2`, `FIXED3` с разными версиями исходников.
5. Сделай Commit и открой **Actions → Build GromVisuals**.
6. После успешной сборки скачай артефакт **GromVisuals-1.0.1**. Основной файл — `GromVisuals-1.0.1.jar`.

Если хочешь начать с чистой структуры, можешь вместо вложенной папки положить `build.gradle`, `settings.gradle`, `gradle.properties` и `src/` прямо в корень репозитория — workflow поддерживает оба варианта.

## Технические параметры

- Minecraft: `1.21.11`
- Java: `21`
- Fabric Loader: `0.18.4+`
- Fabric API: `0.141.6+1.21.11`
- Fabric Loom: `1.14.10`
- Gradle: `9.2.1`
- Mod ID: `gromvisuals`

Полную сборку JAR необходимо подтвердить запуском GitHub Actions. Это исправление закрывает девять несовместимостей API, показанных в логе #45; окончательный результат компиляции пока не подтверждён.
