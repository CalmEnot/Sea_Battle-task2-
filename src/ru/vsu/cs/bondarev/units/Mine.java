package ru.vsu.cs.bondarev.units;

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
    public String getStatus() {
        StringBuilder str = new StringBuilder();
        str.append("Мина; ").append("x - ").append(x[0] + 1).append(" y - ")
                .append(y[0] + 1).append("; Размер - ").append(size).append("; Знак - ").append(sign)
                .append("; Здоровье - ").append(health);
        return str.toString();
    }

    // Генерация Mine с рандомными параметрами
    public static Mine genUnit(int fieldSize) {
        return new Mine((int) (Math.random() * (fieldSize)), (int) (Math.random() * fieldSize));

    }
}
