package ru.vsu.cs.bondarev.units;

import ru.vsu.cs.bondarev.app.BattleField;
import ru.vsu.cs.bondarev.app.Player;

import java.util.List;
import java.util.Scanner;

public class Submarine extends Unit {
    private boolean vertical;
    public Submarine(int x, int y, boolean vertical) {
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
        this.sign = "[$]";
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
        str.append("Подводная лодка");
        if (!hide) {
            str.append("; x - ").append(x[0] + 1).append(" y - ")
                    .append(y[0] + 1).append("; Вертикальный - ").append(vertical)
                    .append("; Размер - ").append(size).append("; Знак - ").append(sign);
        }
        str.append("; Здоровье - ").append(health);
        return str.toString();
    }
    @Override
    public boolean move(int x, int y, Player player1, Player player2) {
        if (!this.getCanMove()) {
            System.out.println("У подводной лодки сломан двигатель!");
            return false;
        }
        BattleField field = player1.getBattleField();
        List<Unit> units = player1.getUnits();
        int[] mineP = new int[] {this.getX()[1], this.getY()[1]};
        // Проверка координат
        if (vertical) {
            if (Math.abs(this.getY()[0] - y) < 2 && this.getX()[0] == x) {
                System.out.println("Слишком малое расстояния для того, чтобы можно было поставить мину!");
                return false;
            }
        } else {
            if (Math.abs(this.getX()[0] - x) < 2 && this.getY()[0] == y) {
                System.out.println("Слишком малое расстояния для того, чтобы можно было поставить мину!");
                return false;
            }
        }
        if (field.tryMoveUnit(x, y, player1.getBattleField(), this, this.vertical)) {
            Mine newMine = new Mine(mineP[0], mineP[1]);
            field.addUnit(newMine);
            units.add(0, newMine);
            return true;
        } else {
            return false;
        }
    }
    // Генерация Submarine с рандомными параметрами
    public static Submarine genUnit(int fieldSize) {
        boolean vertical;
        if (Math.random() * 10 < 5) {
            vertical = true;
        } else {
            vertical = false;
        }
        return new Submarine((int) (Math.random() * (fieldSize)), (int) (Math.random() * fieldSize), vertical);
    }

    @Override
    public boolean attackByRocket(int x, int y, Player player, Player enemy) {
        System.out.println("Подводная лодка не может пустить ракету!");
        return false;
    }

    @Override
    public boolean canAttackByRocket() {
        System.out.println("Подводная лодка не может пустить ракету!");
        return false;
    }
}
