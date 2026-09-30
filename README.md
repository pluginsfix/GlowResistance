# GlowResistance

Плагин для серверов Minecraft (Paper 1.21+), исправляющий механику взаимодействия зарядов ветра (Wind Charge, Breeze Wind Charge, Wind Burst) с чарами взрывоустойчивости (Blast Protection).

## Описание

В ванильной механике Minecraft зачарование «Взрывоустойчивость» (Blast Protection) снижает или полностью блокирует импульс отталкивания при взрывах, включая заряды ветра и зачарование «Ветровой шквал» (Wind Burst). Из-за этого игроки в броне на взрывоустойчивость не могут совершать прыжки на зарядах ветра (wind charge jumping) и не получают должного отталкивания в бою.

**GlowResistance** восстанавливает полноценный физический импульс зарядов ветра независимо от надетой брони и уровня взрывоустойчивости.

## Возможности

- Полное сохранение отталкивания и высоты прыжков на зарядах ветра для игроков в броне со взрывоустойчивостью.
- Поддержка снарядов игрока (`WindCharge`) и бриза (`BreezeWindCharge`).
- Поддержка булавы с чарами «Ветровой шквал» (`WIND_BURST`).
- Гибкая настройка множителей вертикального и горизонтального отталкивания в `config.yml`.
- Поддержка перезагрузки конфигурации без перезапуска сервера.

## Команды и права

- `/glowresistance` — просмотр информации и справки по командам (право: `glowresistance.use`).
- `/glowresistance reload` — перезагрузка файлов `config.yml` и `messages.yml` (право: `glowresistance.admin.reload`).

## Конфигурация

```yaml
config-version: 1

settings:
  ignore-blast-protection: true
  ignore-explosion-knockback-resistance: true
  knockback-multiplier: 1.0
  vertical-multiplier: 1.0
  horizontal-multiplier: 1.0
  player-wind-charge-enabled: true
  breeze-wind-charge-enabled: true
  mace-wind-burst-enabled: true
```
