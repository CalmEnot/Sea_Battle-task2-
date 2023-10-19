package ru.vsu.cs.bondarev.units;

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
    public String getStatus() {
        StringBuilder str = new StringBuilder();
        str.append("Подводная лодка; ").append("x - ").append(x[0] + 1).append(" y - ")
                .append(y[0] + 1).append("; Вертикальный - ").append(vertical)
                .append("; Размер - ").append(size).append("; Знак - ").append(sign)
                .append("; Здоровье - ").append(health);
        return str.toString();
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
}
