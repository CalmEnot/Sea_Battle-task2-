package ru.vsu.cs.bondarev.units;

import ru.vsu.cs.bondarev.app.BattleField;
import ru.vsu.cs.bondarev.app.Game;
import ru.vsu.cs.bondarev.app.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Unit implements UnitInterface {
    protected int[] x;
    protected int[] y;
    protected int size;
    protected String sign;
    protected int health;
    protected boolean canMove;

    public abstract String getStatus(boolean hide);
    public abstract boolean move(int x, int y, Player player1, Player player2);
    public int getSize() {
        return size;
    }

    public String getSign() {
        return sign;
    }

    public int[] getX() {
        return x;
    }
    public void setX(int[] x) {
        this.x = x;
    }

    public int[] getY() {
        return y;
    }
    public void setY(int[] y) {
        this.y = y;
    }
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean getCanMove() {
        return canMove;
    }
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
    public boolean canAttackByRocket() {
        return true;
    }

    public boolean canAttackByTor() {
        return true;
    }

    public boolean attackByRocket(int x, int y, Player player, Player enemy) {
        if (health < 1) {
            return false;
        }
        Unit checkUnit = enemy.getUnitByPoint(x, y);
        // Проверка, попал ли я по врагу
        if (checkUnit != null) {
            // Проверка, стреляю ли я по клетке, которая уже была помечена
            String checkGrid = enemy.getBattleField().getField()[y][x];
            if (checkGrid.equals("[!]") || checkGrid.equals("[X]") || checkGrid.equals("[?]")) {
                System.out.println("Промах!");
                return false;
            }
            //  Если попал по мине
            if (checkGrid.equals("[*]")) {
                markMine(checkUnit, player, enemy);
                return false;
            }
            checkUnit.setHealth(checkUnit.getHealth() - 1);
            checkUnit.setCanMove(false);
            // Проверка здоровья
            if (checkUnit.getHealth() < 1) {
                enemy.getBattleField().markDestroy(checkUnit);
                enemy.setCountLiveUnits(enemy.getCountLiveUnits() - 1);
            } else {
                enemy.getBattleField().markHit(x, y);
            }
            return true;
        }
        System.out.println("Промах!");
        enemy.getBattleField().markMiss(x, y);
        return false;
    }

    public boolean attackByTorpedo(Player player, Player enemy, int y) {
        if (health < 1) {
            return false;
        }
        // Атака торпедой
        Unit checkUnit;
        for (int i = 0; i < enemy.getBattleField().getField().length; i++) {
            checkUnit = enemy.getUnitByPoint(i, y);
            if (checkUnit != null) {
                if (checkUnit.getHealth() != 0) {
                    enemy.getBattleField().markDestroy(checkUnit);
                    checkUnit.setHealth(0);
                    checkUnit.setCanMove(false);
                    if (checkUnit.getSign().equals("[*]")) {
                        System.out.println("О нет, игрок попал на мину!");
                        markMine(checkUnit, player, enemy);
                        return true;
                    }
                    enemy.setCountLiveUnits(enemy.getCountLiveUnits() - 1);
                    return true;
                }
            }
        }
        return false;
    }

    protected void markMine(Unit checkUnit, Player player, Player enemy) {
        int randomPoint;
        while (true) {
            randomPoint = (int) (Math.random() * this.getSize());
            if (randomPoint > this.getSize() - 1) {
                continue;
            }
            String checkGrid = player.getBattleField().getField()[this.x[randomPoint]][this.y[randomPoint]];
            if (checkGrid.equals("[!]") || checkGrid.equals("[X]")) {
                continue;
            }
            break;
        }
        // Помечаем мину
        enemy.getBattleField().markDestroy(checkUnit);
        checkUnit.setHealth(0);
        // Помечаем ранение
        this.setHealth(this.getHealth() - 1);
        this.setCanMove(false);
        if (this.getHealth() < 1) {
            player.getBattleField().markDestroy(this);
            player.setCountLiveUnits(player.getCountLiveUnits() - 1);
        } else {
            player.getBattleField().markHit(this.getX()[randomPoint], this.getY()[randomPoint]);
        }
        System.out.print("Корабль (");
        System.out.print(this.getStatus(false));
        System.out.println(") был подбит!");
    }

    public boolean canAttackTorpedo(int y) {
        if (health < 1) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (getY()[i] == y) {
                return true;
            }
        }
        return false;
    }
}
