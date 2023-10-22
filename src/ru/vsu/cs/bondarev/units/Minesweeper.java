package ru.vsu.cs.bondarev.units;

import ru.vsu.cs.bondarev.app.BattleField;
import ru.vsu.cs.bondarev.app.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Minesweeper extends Unit {
    private boolean vertical;
    public Minesweeper(int x, int y, boolean vertical) {
        int[] xs = new int[3];
        int[] ys = new int[3];
        // Vertical
        if (vertical) {
            for (int i = 0; i < 3; i++) {
                xs[i] = x;
                ys[i] = y + i;
            }
            // Horizontal
        } else {
            for (int i = 0; i < 3; i++) {
                xs[i] = x + i;
                ys[i] = y;
            }
        }
        this.sign = "[#]";
        this.x = xs;
        this.y = ys;
        this.size = 3;
        this.vertical = vertical;
        this.health = size;
        this.canMove = true;
    }
    // Получение информации об корабле
    @Override
    public String getStatus(boolean hide) {
        StringBuilder str = new StringBuilder();
        str.append("Тральщик");
        if (!hide) {
            str.append("; x - ").append(x[0] + 1).append(" y - ")
                    .append(y[0] + 1).append("; Вертикальный - ").append(vertical)
                    .append("; Размер - ").append(size).append("; Знак - ").append(sign);
        }
        str.append("; Здоровье - ").append(health);
        return str.toString();
    }
    @Override
    public boolean move(int x, int y,  Player player1, Player player2) {
        if (!this.getCanMove()) {
            System.out.println("У тральщика сломан двигатель!");
            return false;
        }
        BattleField field = player1.getBattleField();
        if (field.tryMoveUnit(x, y, player1.getBattleField(), this, this.vertical)) {
            List<int[]> checkPoints = new ArrayList<>();
            // Добавление точек для проверки вокруг тральщика мин
            if (vertical) {
                checkPoints.add(new int[]{this.getX()[0] - 1, this.getY()[0] - 1});
                checkPoints.add(new int[]{this.getX()[0] - 1, this.getY()[0]});
                checkPoints.add(new int[]{this.getX()[0], this.getY()[0] - 1});
                checkPoints.add(new int[]{this.getX()[0], this.getY()[0]});
                checkPoints.add(new int[]{this.getX()[0] + 1, this.getY()[0] - 1});
                checkPoints.add(new int[]{this.getX()[0] + 1, this.getY()[0]});
                checkPoints.add(new int[]{this.getX()[1] - 1, this.getY()[1]});
                checkPoints.add(new int[]{this.getX()[1], this.getY()[1]});
                checkPoints.add(new int[]{this.getX()[1] + 1, this.getY()[1]});
                checkPoints.add(new int[]{this.getX()[2] - 1, this.getY()[2] + 1});
                checkPoints.add(new int[]{this.getX()[2] - 1, this.getY()[2]});
                checkPoints.add(new int[]{this.getX()[2], this.getY()[2] + 1});
                checkPoints.add(new int[]{this.getX()[2], this.getY()[2]});
                checkPoints.add(new int[]{this.getX()[2] + 1, this.getY()[2] + 1});
                checkPoints.add(new int[]{this.getX()[2] + 1, this.getY()[2]});
            } else {
                checkPoints.add(new int[]{this.getX()[0] - 1, this.getY()[0] - 1});
                checkPoints.add(new int[]{this.getX()[0] - 1, this.getY()[0]});
                checkPoints.add(new int[]{this.getX()[0] - 1, this.getY()[0] + 1});
                checkPoints.add(new int[]{this.getX()[0], this.getY()[0] - 1});
                checkPoints.add(new int[]{this.getX()[0], this.getY()[0]});
                checkPoints.add(new int[]{this.getX()[0], this.getY()[0] + 1});
                checkPoints.add(new int[]{this.getX()[1], this.getY()[0] - 1});
                checkPoints.add(new int[]{this.getX()[1], this.getY()[0]});
                checkPoints.add(new int[]{this.getX()[1], this.getY()[0] + 1});
                checkPoints.add(new int[]{this.getX()[2], this.getY()[0] - 1});
                checkPoints.add(new int[]{this.getX()[2], this.getY()[0]});
                checkPoints.add(new int[]{this.getX()[2], this.getY()[0] + 1});
                checkPoints.add(new int[]{this.getX()[2] + 1, this.getY()[0] - 1});
                checkPoints.add(new int[]{this.getX()[2] + 1, this.getY()[0]});
                checkPoints.add(new int[]{this.getX()[2] + 1, this.getY()[0] + 1});
            }
            // Проверка мин
            for (int i = 0; i < checkPoints.size(); i++) {
                if (checkPoints.get(i)[0] + 1 > field.getSize() || checkPoints.get(i)[0] < 0
                        || checkPoints.get(i)[1] + 1 > field.getSize() || checkPoints.get(i)[1] < 0) {
                    continue;
                }
                int[] checkPoint = checkPoints.get(i);
                if (player2.getBattleField().getField()[checkPoint[1]][checkPoint[0]].equals("[*]")) {
                    System.out.println("Была уничтожена мина!");
                    Unit mine = player2.getUnitByPoint(checkPoint[0], checkPoint[1]);
                    player2.getBattleField().markDestroy(mine);
                    mine.setHealth(0);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean attackByTorpedo(Player player, Player enemy, int y) {
        return false;
    }
    // Генерация Minesweeper с рандомными параметрами
    public static Minesweeper genUnit(int fieldSize) {
        boolean vertical;
        if (Math.random() * 10 < 5) {
            vertical = true;
        } else {
            vertical = false;
        }
        return new Minesweeper((int) (Math.random() * (fieldSize)), (int) (Math.random() * fieldSize), vertical);
    }

    @Override
    public boolean canAttackByTor() {
        System.out.println("Тральщик не может пустить торпеду!");
        return false;
    }
}
