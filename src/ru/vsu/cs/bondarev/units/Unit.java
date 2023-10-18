package ru.vsu.cs.bondarev.units;

public abstract class Unit {
    protected int[] x;
    protected int[] y;
    protected int size;
    protected String sign;
    protected int health;

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

    public int[] getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int value) {
        health = value;
    }
}
