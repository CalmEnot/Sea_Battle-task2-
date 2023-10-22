package ru.vsu.cs.bondarev.app;


import ru.vsu.cs.bondarev.app.BattleField;
import ru.vsu.cs.bondarev.units.*;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.Arrays;
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

        // Создание мины
        Unit mine = Mine.genUnit(field.getSize());
        while (!field.canMakeUnit(mine)) {
            mine = Mine.genUnit(field.getSize());
        }
        units.add(mine);
        field.addUnit(mine);
        // Создание подводной лодки
        Unit submarine = Submarine.genUnit(field.getSize());
        // Проверка, могу ли я его поместить на карту
        while (!field.canMakeUnit(submarine)) {
            submarine = Submarine.genUnit(field.getSize());
        }
        units.add(submarine);
        countLiveUnits++;
        field.addUnit(submarine);

        // Создание тральщика
        Unit minesweeper = Minesweeper.genUnit(field.getSize());
        // Проверка, могу ли я его поместить на карту
        while (!field.canMakeUnit(minesweeper)) {
            minesweeper = Minesweeper.genUnit(field.getSize());
        }
        units.add(minesweeper);
        countLiveUnits++;
        field.addUnit(minesweeper);

        // Генерация основных кораблей
        for (int i = 0; i < count - 3; i++) {
            // Создание обязательно кораблей каждого размера
            if (i + 1 < 5) {
                Unit battleShip = Battleship.genUnit(i + 1, field.getSize());
                // Проверка, могу ли я его поместить на карту
                while (!field.canMakeUnit(battleShip)) {
                    battleShip = Battleship.genUnit(i + 1, field.getSize());
                }
                units.add(battleShip);
                countLiveUnits++;
                field.addUnit(battleShip);
                continue;
            }
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
    public String getPlayerStatus(int deltaCntUnit, boolean hide) {
        StringBuilder str = new StringBuilder();
        str.append("Имя: ").append(name).append("\n");
        str.append("Количество юнитов: ").append(units.size()).append(", живых - ")
                .append(countLiveUnits).append("\n");
        for (int i = 0; i < units.size(); i++) {
            str.append(i + 1).append(". ").append(units.get(i).getStatus(hide)).append("\n");
        }
        // Пропуск строки, если у игроков разное количество юнитов
        if (deltaCntUnit > 0) {
            for (int i = 0; i < deltaCntUnit; i++) {
                str.append("\n");
            }
        }
        str.append("Поле:\n");
        str.append(field.getFieldStatus(hide));
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

    // Получение корабля по координатам
    public Unit getUnitByPoint(int x, int y) {
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
