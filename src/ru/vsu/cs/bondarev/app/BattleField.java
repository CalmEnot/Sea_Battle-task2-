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

    // Проверка пусто ли в этой клетке
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
    public String getFieldStatus(boolean hide) {
        StringBuilder str = new StringBuilder();
        str.append("   ");
        for (int i = 0; i < field.length; i++) {
            if (i + 1 < 10) {
                str.append(0);
            }
            str.append(i+1).append(" ");
        }
        str.append("\n");
        for (int i = 0; i < field.length; i++) {
            if (i + 1 < 10) {
                str.append(0);
            }
            str.append(i+1).append(" ");
            for (int j = 0; j < field[0].length; j++) {
                if (hide) {
                    if (field[i][j].equals("[#]") || field[i][j].equals("[$]") || field[i][j].equals("[@]") || field[i][j].equals("[*]")) {
                        str.append("[ ]");
                        continue;
                    }
                }
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

    public void delUnit(Unit unit) {
        for (int i = 0; i < unit.getSize(); i++) {
            field[unit.getY()[i]][unit.getX()[i]] = "[ ]";
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

    public void markHit(int x, int y) {
        field[y][x] = "[!]";
    }

    public void markDestroy(Unit unit) {
        int[] x = unit.getX();
        int[] y = unit.getY();
        for (int i = 0; i < x.length; i++) {
            field[y[i]][x[i]] = "[X]";
        }
    }

    public void markMiss(int x, int y) {
        field[y][x] = "[?]";
    }

    public boolean tryMoveUnit(int x, int y, BattleField field, Unit unit, boolean vertical) {
        delUnit(unit);
        int[] oldXP = new int[unit.getSize()];
        int[] oldYP = new int[unit.getSize()];
        for (int i = 0; i < unit.getSize(); i++) {
            oldXP[i] = unit.getX()[i];
            oldYP[i] = unit.getY()[i];
        }
        // Обновляю на новые точки
        for (int i = 0; i < unit.getSize(); i++) {
            if (vertical) {
                unit.getX()[i] = x;
                unit.getY()[i] = y + i;
                if (x > field.getSize() - 1 || y > field.getSize() - 1) {
                    unit.setX(oldXP);
                    unit.setY(oldYP);
                    addUnit(unit);
                    return false;
                }
            } else {
                unit.getX()[i] = x + i;
                unit.getY()[i] = y;
                if (x > field.getSize() - 1 || y > field.getSize() - 1) {
                    unit.setX(oldXP);
                    unit.setY(oldYP);
                    addUnit(unit);
                    return false;
                }
            }
        }
        // Если могу переместить
        if (canMakeUnit(unit)) {
            // Перемещаю
            addUnit(unit);
            return true;
        }
        // Если не могу
        unit.setX(oldXP);
        unit.setY(oldYP);
        addUnit(unit);
        return false;
    }
}
