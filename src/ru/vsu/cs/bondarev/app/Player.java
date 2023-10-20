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
    public String getPlayerStatus(int deltaCntUnit) {
        StringBuilder str = new StringBuilder();
        str.append("Имя: ").append(name).append("\n");
        str.append("Количество юнитов: ").append(units.size()).append(", живых - ")
                .append(countLiveUnits).append("\n");
        for (int i = 0; i < units.size(); i++) {
            str.append(i + 1).append(". ").append(units.get(i).getStatus()).append("\n");
        }
        // Пропуск строки, если у игроков разное количество юнитов
        if (deltaCntUnit > 0) {
            for (int i = 0; i < deltaCntUnit; i++) {
                str.append("\n");
            }
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

    public String getHidePlayerStatus(int deltaCntUnit) {
        StringBuilder str = new StringBuilder();
        str.append("Имя: ").append(name).append("\n");
        str.append("Количество юнитов: ").append(units.size()).append(", живых - ")
                .append(countLiveUnits).append("\n");
        for (int i = 0; i < units.size(); i++) {
            str.append(i + 1).append(". ").append(units.get(i).getHideStatus()).append("\n");
        }
        // Пропуск строки, если у игроков разное количество юнитов
        if (deltaCntUnit > 0) {
            for (int i = 0; i < deltaCntUnit; i++) {
                str.append("\n");
            }
        }
        str.append("Поле:\n");
        str.append(field.getHideFieldStatus());
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
            // Проверка, попал ли на мину
            if (checkGrid.equals("[*]")) {
                System.out.println("О нет! Игрок попал на мину!");
                // Выбор случайной непомеченной ячейки для атаки
                int randomUnit;
                int randomPoint;
                while (true) {
                    randomUnit = (int) (Math.random() * getUnits().size());
                    while (randomUnit > getUnits().size() - 1) {
                        randomUnit = (int) (Math.random() * getUnits().size());
                    }
                    if (units.get(randomUnit).getHealth() < 1 || units.get(randomUnit).getSign().equals("[*]")) {
                        continue;
                    }
                    randomPoint = (int) (Math.random() * getUnits().get(randomUnit).getSize());
                    while (randomPoint > getUnits().get(randomUnit).getSize() - 1) {
                        randomPoint = (int) (Math.random() * getUnits().get(randomUnit).getSize());
                    }
                    String unitGrid = field.getField()[units.get(randomUnit).getY()[randomPoint]][units.get(randomUnit).getX()[randomPoint]];
                    if (unitGrid.equals("[!]")) {
                        continue;
                    }
                    break;
                }
                // Помечаем, что мина уничтожена
                markDestroy(checkUnit, player.getBattleField());
                checkUnit.setHealth(checkUnit.getHealth() - 1);
                // Помечаем подбитый корабль
                Unit markUnit = units.get(randomUnit);
                markUnit.setHealth(markUnit.getHealth() - 1);
                markUnit.setCanMove(false);
                if (markUnit.getHealth() < 1) {
                    markDestroy(markUnit, field);
                    setCountLiveUnits(getCountLiveUnits() - 1);
                } else {
                    markHit(markUnit.getX()[randomPoint], markUnit.getY()[randomPoint], field);
                }
                System.out.print("Корабль ");
                System.out.print(name);
                System.out.print(" (");
                System.out.print(getUnits().get(randomUnit).getStatus());
                System.out.println(") был подбит!");
                return false;
            }

            checkUnit.setHealth(checkUnit.getHealth() - 1);
            checkUnit.setCanMove(false);
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
    public boolean moveBattleShip(int n, int m) {
        Unit checkUnit = units.get(n - 1);
        // Проверка, могу ли я переместить юнит
        if (!checkUnit.getCanMove()) {
            return false;
        }
//        if (checkUnit.getSize() != checkUnit.getHealth()) {
//            return false;
//        }
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

    // Перемещение подводной лодки или тральщика
    public boolean moveSpecUnit(int n, int x, int y, Player enemy) {

        Unit checkUnit = units.get(n - 1);
        // Проверка, могу ли я переместить юнит
        if (!checkUnit.getCanMove()) {
            return false;
        }
        // Подводная людка
        if (checkUnit.getSign().equals("[$]")) {
            int[] mineP = new int[] {checkUnit.getX()[1], checkUnit.getY()[1]};
            boolean vertical;
            if (checkUnit.getX()[1] - checkUnit.getX()[0] == 1) {
                vertical = false;
            } else {
                vertical = true;
            }
            // Проверка координат
            if (vertical) {
                if (Math.abs(checkUnit.getY()[0] - y) < 2 && checkUnit.getX()[0] == x) {
                    System.out.println("Слишком малое расстояния для того, чтобы можно было поставить мину!");
                    return false;
                }
            } else {
                if (Math.abs(checkUnit.getX()[0] - x) < 2 && checkUnit.getY()[0] == y) {
                    System.out.println("Слишком малое расстояния для того, чтобы можно было поставить мину!");
                    return false;
                }
            }
            field.delUnit(checkUnit);
            int[] oldXP = new int[] {checkUnit.getX()[0], checkUnit.getX()[1], checkUnit.getX()[2]};
            int[] oldYP = new int[] {checkUnit.getY()[0], checkUnit.getY()[1], checkUnit.getY()[2]};
            // Обновляю на новые точки
            for (int i = 0; i < checkUnit.getSize(); i++) {
                if (vertical) {
                    checkUnit.getX()[i] = x;
                    checkUnit.getY()[i] = y + i;
                } else {
                    checkUnit.getX()[i] = x + i;
                    checkUnit.getY()[i] = y;
                }
            }
            // Если могу переместить подводную лодку
            if (field.canMakeUnit(checkUnit)) {
                // Создаю мину
                Mine newMine = new Mine(mineP[0], mineP[1]);
                field.addUnit(newMine);
                units.add(0, newMine);
                // Перемещаю подводную лодку
                field.addUnit(checkUnit);
                return true;
            }
            // Если не могу
            checkUnit.setX(oldXP);
            checkUnit.setY(oldYP);
            field.addUnit(checkUnit);
            return false;
        }
        // Тральщик
        else {
            field.delUnit(checkUnit);
            int[] oldXP = new int[] {checkUnit.getX()[0], checkUnit.getX()[1], checkUnit.getX()[2]};
            int[] oldYP = new int[] {checkUnit.getY()[0], checkUnit.getY()[1], checkUnit.getY()[2]};
            boolean vertical;
            // Обновляю на новые точки
            if (oldXP[1] - oldXP[0] == 1) {
                vertical = false;
            } else {
                vertical = true;
            }
            for (int i = 0; i < checkUnit.getSize(); i++) {
                if (vertical) {
                    checkUnit.getX()[i] = x;
                    checkUnit.getY()[i] = y + i;
                } else {
                    checkUnit.getX()[i] = x + i;
                    checkUnit.getY()[i] = y;
                }
            }
            // Если могу переместить тральщик
            if (field.canMakeUnit(checkUnit)) {
                // Перемещаю тральщик
                List<int[]> checkPoints = new ArrayList<>();
                // Добавление точек для проверки вокруг тральщика мин
                if (vertical) {
                    checkPoints.add(new int[]{checkUnit.getX()[0] - 1, checkUnit.getY()[0] - 1});
                    checkPoints.add(new int[]{checkUnit.getX()[0] - 1, checkUnit.getY()[0]});
                    checkPoints.add(new int[]{checkUnit.getX()[0], checkUnit.getY()[0] - 1});
                    checkPoints.add(new int[]{checkUnit.getX()[0], checkUnit.getY()[0]});
                    checkPoints.add(new int[]{checkUnit.getX()[0] + 1, checkUnit.getY()[0] - 1});
                    checkPoints.add(new int[]{checkUnit.getX()[0] + 1, checkUnit.getY()[0]});
                    checkPoints.add(new int[]{checkUnit.getX()[1] - 1, checkUnit.getY()[1]});
                    checkPoints.add(new int[]{checkUnit.getX()[1], checkUnit.getY()[1]});
                    checkPoints.add(new int[]{checkUnit.getX()[1] + 1, checkUnit.getY()[1]});
                    checkPoints.add(new int[]{checkUnit.getX()[2] - 1, checkUnit.getY()[2] + 1});
                    checkPoints.add(new int[]{checkUnit.getX()[2] - 1, checkUnit.getY()[2]});
                    checkPoints.add(new int[]{checkUnit.getX()[2], checkUnit.getY()[2] + 1});
                    checkPoints.add(new int[]{checkUnit.getX()[2], checkUnit.getY()[2]});
                    checkPoints.add(new int[]{checkUnit.getX()[2] + 1, checkUnit.getY()[2] + 1});
                    checkPoints.add(new int[]{checkUnit.getX()[2] + 1, checkUnit.getY()[2]});
                } else {
                    checkPoints.add(new int[]{checkUnit.getX()[0] - 1, checkUnit.getY()[0] - 1});
                    checkPoints.add(new int[]{checkUnit.getX()[0] - 1, checkUnit.getY()[0]});
                    checkPoints.add(new int[]{checkUnit.getX()[0] - 1, checkUnit.getY()[0] + 1});
                    checkPoints.add(new int[]{checkUnit.getX()[0], checkUnit.getY()[0] - 1});
                    checkPoints.add(new int[]{checkUnit.getX()[0], checkUnit.getY()[0]});
                    checkPoints.add(new int[]{checkUnit.getX()[0], checkUnit.getY()[0] + 1});
                    checkPoints.add(new int[]{checkUnit.getX()[1], checkUnit.getY()[0] - 1});
                    checkPoints.add(new int[]{checkUnit.getX()[1], checkUnit.getY()[0]});
                    checkPoints.add(new int[]{checkUnit.getX()[1], checkUnit.getY()[0] + 1});
                    checkPoints.add(new int[]{checkUnit.getX()[2], checkUnit.getY()[0] - 1});
                    checkPoints.add(new int[]{checkUnit.getX()[2], checkUnit.getY()[0]});
                    checkPoints.add(new int[]{checkUnit.getX()[2], checkUnit.getY()[0] + 1});
                    checkPoints.add(new int[]{checkUnit.getX()[2] + 1, checkUnit.getY()[0] - 1});
                    checkPoints.add(new int[]{checkUnit.getX()[2] + 1, checkUnit.getY()[0]});
                    checkPoints.add(new int[]{checkUnit.getX()[2] + 1, checkUnit.getY()[0] + 1});
                }
                // Проверка мин
                for (int i = 0; i < checkPoints.size(); i++) {
                    if (checkPoints.get(i)[0] + 1 > field.getSize() || checkPoints.get(i)[0] < 0
                            || checkPoints.get(i)[1] + 1 > field.getSize() || checkPoints.get(i)[1] < 0) {
                        continue;
                    }
                    int[] checkPoint = checkPoints.get(i);
                    if (enemy.field.getField()[checkPoint[1]][checkPoint[0]].equals("[*]")) {
                        System.out.println("Была уничтожена мина!");
                        Unit mine = enemy.getUnitByPoint(checkPoint[0], checkPoint[1]);
                        enemy.markDestroy(mine, enemy.getBattleField());
                        mine.setHealth(0);
                    }
                }
                field.addUnit(checkUnit);
                return true;
            }
            // Если не могу
            checkUnit.setX(oldXP);
            checkUnit.setY(oldYP);
            field.addUnit(checkUnit);
            return false;
        }
    }

    public boolean canAttackByTorpedo(int y) {
        for (int i = 0; i < field.getSize(); i++) {
            if (getUnitByPoint(i, y) != null) {
                if (!getUnitByPoint(i, y).getSign().equals("[*]") && !field.getField()[y][i].equals("[X]")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean attackTorpedo(int y, Player player) {
        Unit checkUnit;
        for (int i = 0; i < player.getBattleField().getField().length; i++) {
            checkUnit = player.getUnitByPoint(i, y);
            if (checkUnit != null) {
                if (checkUnit.getHealth() != 0) {
                    markDestroy(checkUnit, player.getBattleField());
                    checkUnit.setHealth(0);
                    if (!checkUnit.getSign().equals("[*]")) {
                        checkUnit.setCanMove(false);
                        player.setCountLiveUnits(player.getCountLiveUnits() - 1);
                    }
                    return true;
                }
            }
        }
        return false;
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
