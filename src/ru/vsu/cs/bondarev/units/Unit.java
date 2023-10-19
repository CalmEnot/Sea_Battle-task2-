package ru.vsu.cs.bondarev.units;

public abstract class Unit implements UnitInterface {
    protected int[] x;
    protected int[] y;
    protected int size;
    protected String sign;
    protected int health;
    protected boolean canMove;

    public abstract String getStatus();
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
}
