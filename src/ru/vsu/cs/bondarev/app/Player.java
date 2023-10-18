package ru.vsu.cs.bondarev.app;


import ru.vsu.cs.bondarev.app.BattleField;
import ru.vsu.cs.bondarev.units.Battleship;
import ru.vsu.cs.bondarev.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Unit> units;
    private int countLiveUnits;
    private BattleField field;

    public Player(String name, int sizeField) {
        this.name = name;
        this.countLiveUnits = 0;
        this.field = new BattleField(sizeField);
        this.units = new ArrayList<>();
    }

    public BattleField getBattleField() {
        return field;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setCountLiveUnits(int value) {
        countLiveUnits = value;
    }

    public int getCountLiveUnits() {
        return countLiveUnits;
    }

    // Размещение юнитов по полю в рандомные места
    public void genUnits(int count) {
        int sizeF = field.getSize();
        field = new BattleField(sizeF);
        units = new ArrayList<>();
        countLiveUnits = 0;
        for (int i = 0; i < count; i++) {
            Unit battleShip = Battleship.genUnit(-1, field.getSize());
            // Проверка, могу ли я его поместить на карту
            while (!field.canMakeUnit(battleShip)) {
                battleShip = Battleship.genUnit(-1, field.getSize());
            }
            units.add(battleShip);
            countLiveUnits++;
            field.addUnit(battleShip);
        }
    }

    // Получение информации о юнитах и карте игрока
    public String getPlayerStatus() {
        StringBuilder str = new StringBuilder();
        str.append("Имя: ").append(name).append("\n");
        str.append("Количество юнитов: ").append(units.size()).append(", живых - ")
                .append(countLiveUnits).append("\n");

        for (int i = 0; i < units.size(); i++) {
            str.append(i + 1).append(". ").append(units.get(i).getStatus()).append("\n");
        }
        str.append("Поле:\n");
        str.append(field.getFieldStatus());
        // Добавление пробелов для каждой строки, чтобы количество символов было одинаковым
        String[] editStrings = str.toString().split("\n");
        int maxString = 0;
        for (int i = 0; i < editStrings.length; i++) {
            if (editStrings[i].length() > maxString) {
                maxString = editStrings[i].length();
            }
        }

        for (int i = 0; i < editStrings.length; i++) {
            while (editStrings[i].length() != maxString) {
                editStrings[i] += " ";
            }
        }
        return String.join("\n", editStrings);
    }
    // Атака торпедой может быть по y такая, какая есть y у кораблей
    // Атака другого игрока
    public boolean attackByRocket(int x, int y, Player player) {
        Unit checkUnit = player.getUnitByPoint(x - 1, y - 1);
        // Проверка, попал ли я по врагу
        if (checkUnit != null) {
            // Проверка, стреляю ли я по клетке, которая уже была помечена
            String checkGrid = player.getBattleField().getField()[y - 1][x - 1];
            if (checkGrid.equals("[!]") || checkGrid.equals("[X]") || checkGrid.equals("[?]")) {
                return false;
            }
            checkUnit.setHealth(checkUnit.getHealth() - 1);
            // Проверка здоровья
            if (checkUnit.getHealth() < 1) {
                markDestroy(checkUnit, player.getBattleField());
                player.setCountLiveUnits(player.getCountLiveUnits() - 1);
            } else {
                markHit(x - 1, y - 1, player.getBattleField());
            }
            return true;
        }
        markMiss(x - 1, y - 1, player.getBattleField());
        return false;
    }

    // Пометить на карте попадание
    private void markHit(int x, int y, BattleField field) {
        field.getField()[y][x] = "[!]";
    }

    private void markDestroy(Unit unit, BattleField field) {
        int[] x = unit.getX();
        int[] y = unit.getY();
        for (int i = 0; i < x.length; i++) {
            field.getField()[y[i]][x[i]] = "[X]";
        }
    }

    private void markMiss(int x, int y, BattleField field) {
        field.getField()[y][x] = "[?]";
    }

    // Перемещение юнитов
    public boolean moveUnit(int n, int m) {
        Unit checkUnit = units.get(n - 1);
        // Проверка, могу ли я переместить юнит
        if (!checkUnit.getCanMove()) {
            return false;
        }
        if (checkUnit.getSize() != checkUnit.getHealth()) {
            return false;
        }
        // Перемещение влево или вверх
        if (m == 1) {
            // Если размер юнита = 1, то он рандомно может выбрать направление перемещения
            if (checkUnit.getSize() == 1) {
                // Влево
                if (Math.random() * 10 < 5) {
                    // Проверка выход за границу поля
                    if (checkUnit.getX()[0] - 1 < 0) {
                        return false;
                    }
                    // Проверка на перемещение на непустую клетку
                    if (!field.checkGrid(checkUnit.getX()[0] - 1, checkUnit.getY()[0])) {
                        return false;
                    }
                    field.delUnit(checkUnit);
                    checkUnit.getX()[0] -= 1;
                    field.addUnit(checkUnit);
                    return true;
                }
                // Вверх
                else {
                    if (checkUnit.getY()[0] - 1 < 0) {
                        return false;
                    }
                    // Проверка на перемещение на непустую клетку
                    if (!field.checkGrid(checkUnit.getX()[0], checkUnit.getY()[0] - 1)) {
                        return false;
                    }
                    field.delUnit(checkUnit);
                    checkUnit.getY()[0] -= 1;
                    field.addUnit(checkUnit);
                    return true;
                }
            }
            // Если размер больше 1
            else {
                // Проверка направления
                // Юнит вертикальный
                if (checkUnit.getY()[1] - checkUnit.getY()[0] == 1) {
                    if (checkUnit.getY()[0] - 1 < 0) {
                        return false;
                    }
                    // Проверка на перемещение на непустую клетку
                    if (!field.checkGrid(checkUnit.getX()[0], checkUnit.getY()[0] - 1)) {
                        return false;
                    }
                    field.delUnit(checkUnit);
                    for (int i = 0; i < checkUnit.getSize(); i++) {
                        checkUnit.getY()[i] -= 1;
                    }
                    field.addUnit(checkUnit);
                    return true;
                }
                // Юнит горизонтальный
                else {
                    if (checkUnit.getX()[0] - 1 < 0) {
                        return false;
                    }
                    // Проверка на перемещение на непустую клетку
                    if (!field.checkGrid(checkUnit.getX()[0] - 1, checkUnit.getY()[0])) {
                        return false;
                    }
                    field.delUnit(checkUnit);
                    for (int i = 0; i < checkUnit.getSize(); i++) {
                        checkUnit.getX()[i] -= 1;
                    }
                    field.addUnit(checkUnit);
                    return true;
                }
            }
        }
        // Перемещение вправо или вниз
        else {
            // Если размер юнита = 1, то он рандомно может выбрать направление перемещения
            if (checkUnit.getSize() == 1) {
                // Вправо
                if (Math.random() * 10 < 5) {
                    // Проверка выход за границу поля
                    if (checkUnit.getX()[0] + 1 > field.getSize() - 1) {
                        return false;
                    }
                    // Проверка на перемещение на непустую клетку
                    if (!field.checkGrid(checkUnit.getX()[0] + 1, checkUnit.getY()[0])) {
                        return false;
                    }
                    field.delUnit(checkUnit);
                    checkUnit.getX()[0] += 1;
                    field.addUnit(checkUnit);
                    return true;
                }
                // Вниз
                else {
                    if (checkUnit.getY()[0] + 1 > field.getSize() - 1) {
                        return false;
                    }
                    if (!field.checkGrid(checkUnit.getX()[0], checkUnit.getY()[0] + 1)) {
                        return false;
                    }
                    field.delUnit(checkUnit);
                    checkUnit.getY()[0] += 1;
                    field.addUnit(checkUnit);
                    return true;
                }
            }
            // Если размер больше 1
            else {
                // Проверка направления
                // Юнит вертикальный
                if (checkUnit.getY()[1] - checkUnit.getY()[0] == 1) {
                    if (checkUnit.getY()[checkUnit.getSize() - 1] + 1 > field.getSize() - 1) {
                        return false;
                    }
                    if (!field.checkGrid(checkUnit.getX()[checkUnit.getSize() - 1], checkUnit.getY()[checkUnit.getSize() - 1] + 1)) {
                        return false;
                    }
                    field.delUnit(checkUnit);
                    for (int i = 0; i < checkUnit.getSize(); i++) {
                        checkUnit.getY()[i] += 1;
                    }
                    field.addUnit(checkUnit);
                    return true;
                }
                // Юнит горизонтальный
                else {
                    if (checkUnit.getX()[checkUnit.getSize() - 1] + 1 > field.getSize() - 1) {
                        return false;
                    }
                    if (!field.checkGrid(checkUnit.getX()[checkUnit.getSize() - 1] + 1, checkUnit.getY()[checkUnit.getSize() - 1])) {
                        return false;
                    }
                    field.delUnit(checkUnit);
                    for (int i = 0; i < checkUnit.getSize(); i++) {
                        checkUnit.getX()[i] += 1;
                    }
                    field.addUnit(checkUnit);
                    return true;
                }
            }
        }
    }
    // Получение корабля по координатам
    private Unit getUnitByPoint(int x, int y) {
        for (int i = 0; i < units.size(); i++) {
            for (int j = 0; j < units.get(i).getSize(); j++) {
                if (x == units.get(i).getX()[j] && y == units.get(i).getY()[j]) {
                    return units.get(i);
                }
            }
        }
        return null;
    }
}
