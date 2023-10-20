package ru.vsu.cs.bondarev.units;

public class Battleship extends Unit {
    private boolean vertical;

    public Battleship(int x, int y, int size, boolean vertical) {
        int[] xs = new int[size];
        int[] ys = new int[size];
        // Vertical
        if (vertical) {
            for (int i = 0; i < size; i++) {
                xs[i] = x;
                ys[i] = y + i;
            }
        // Horizontal
        } else {
            for (int i = 0; i < size; i++) {
                xs[i] = x + i;
                ys[i] = y;
            }
        }
        this.sign = "[@]";
        this.x = xs;
        this.y = ys;
        this.size = size;
        this.vertical = vertical;
        this.health = size;
        this.canMove = true;
    }
    // Получение информации об корабле
    public String getStatus() {
        StringBuilder str = new StringBuilder();
        str.append("Крейсер; ").append("x - ").append(x[0] + 1).append(" y - ")
                .append(y[0] + 1).append("; Вертикальный - ").append(vertical)
                .append("; Размер - ").append(size).append("; Знак - ").append(sign)
                .append("; Здоровье - ").append(health);
        return str.toString();
    }

    public String getHideStatus() {
        StringBuilder str = new StringBuilder();
        str.append("Крейсер; ").append("; Здоровье - ").append(health);
        return str.toString();
    }

    // Генерация BattleShip с рандомными параметрами
    public static Battleship genUnit(int unitSize, int fieldSize) {
        int size = unitSize;
        if (unitSize < 1 || unitSize > 4) {
            size = (int) ((Math.random()) * 4 + 1);
        }
        boolean vertical;
        if (Math.random() * 10 < 5) {
            vertical = true;
        } else {
            vertical = false;
        }
        return new Battleship((int) (Math.random() * (fieldSize)), (int) (Math.random() * fieldSize), size, vertical);

    }
}
