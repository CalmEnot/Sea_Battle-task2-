package ru.vsu.cs.bondarev.app;

import ru.vsu.cs.bondarev.units.Unit;

public class BattleField {
    private String[][] field;
    private final int size;

    // Создаю поле
    public BattleField(int size) {
        this.size = size;
        this.field = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                field[i][j] = "[ ]";
            }
        }
    }

    public String[][] getField() {
        return field;
    }

    // Проверка, можно ли по этим координатам что-нибудь положить
    public boolean checkGrid(int x, int y) {
        if (x < size && y < size) {
            if (field[y][x].equals("[ ]")) {
                return true;
            }
        }
        return false;
    }

    public int getSize() {
        return size;
    }

    // Получение информации о поле
    public String getFieldStatus() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                str.append(field[i][j]);
            }
            str.append("\n");
        }
        return str.toString();
    }
    public void addUnit(Unit unit) {
        for (int i = 0; i < unit.getSize(); i++) {
            field[unit.getY()[i]][unit.getX()[i]] = unit.getSign();
        }
    }

    // Проверка размещения юнита на поле
    public boolean canMakeUnit(Unit unit) {
        for (int i = 0; i < unit.getSize(); i++) {
            if (!checkGrid(unit.getX()[i], unit.getY()[i])) {
                return false;
            }
        }
        return true;
    }
}
