package ru.vsu.cs.bondarev.units;

import ru.vsu.cs.bondarev.app.BattleField;
import ru.vsu.cs.bondarev.app.Player;

import java.util.Scanner;

public class Mine extends Unit{
    public Mine(int x, int y) {
        int[] xs = new int[1];
        int[] ys = new int[1];
        xs[0] = x;
        ys[0] = y;
        this.sign = "[*]";
        this.x = xs;
        this.y = ys;
        this.size = 1;
        this.health = size;
        this.canMove = false;
    }
    // Получение информации об корабле
    @Override
    public String getStatus(boolean hide) {
        StringBuilder str = new StringBuilder();
        str.append("Мина");
        if (!hide) {
            str.append("; x - ").append(x[0] + 1).append(" y - ")
                    .append(y[0] + 1).append("; Размер - ").append(size).append("; Знак - ").append(sign);

        }
        str.append("; Здоровье - ").append(health);
        return str.toString();
    }
    @Override
    public boolean move(int x, int y, Player player1, Player player2) {
        return false;
    }

    // Генерация Mine с рандомными параметрами
    public static Mine genUnit(int fieldSize) {
        return new Mine((int) (Math.random() * (fieldSize)), (int) (Math.random() * fieldSize));
    }

    @Override
    public boolean attackByRocket(int x, int y, Player player, Player enemy) {
        return false;
    }

    @Override
    public boolean attackByTorpedo(Player player, Player enemy, int y) {
        return false;
    }

    @Override
    public boolean canAttackByRocket() {
        System.out.println("Мина не может пустить ракету!");
        return false;
    }

    @Override
    public boolean canAttackByTor() {
        System.out.println("Мина не может пустить торпеду!");
        return false;
    }
}
