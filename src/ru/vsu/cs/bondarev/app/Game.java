package ru.vsu.cs.bondarev.app;

import java.util.Scanner;

public class Game {
    private final String name;
    public Game(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void start() {
        System.out.println("Добро пожаловать в Морской бой!");
        System.out.print("Выберете режим игры (1 - Игра с самим собой; 2 - игра против компьютера; 3 - игра двух ботов): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.equals("1")) {
            gameMode1(scanner);
        }
    }

    // Игра с самим собой
    private void gameMode1(Scanner scanner) {
        System.out.println("Выбран режим: игра с самим собой");
        System.out.println("Фаза игры: расстановка");
        // Фаза расстановки
        System.out.println("Команды: 1 - сгенерировать корабли, 2 - подтвердить расстановку");
        String input = "";
        int sizeF;
        // Выяснение размеров поля
        while (true) {
            try {
                System.out.print("Размеры поля (число, не меньшее 10): ");
                sizeF = Integer.parseInt(scanner.nextLine());
                if (sizeF < 10) {
                    System.out.println("Недопустимые размеры поля!");
                    continue;
                }
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
            }
        }
        System.out.print("Имя первого игрока: ");
        Player player1 = new Player(scanner.nextLine(), sizeF);
        System.out.println("Расстановка первого игрока: ");
        System.out.println(player1.getPlayerStatus());
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player1.genUnits(6);
            } else if (input.equals("2")) {
                if (player1.getCountLiveUnits() < 1) {
                    System.out.println("Невозможно продолжить, так как не было расстановки!");
                    continue;
                }
                System.out.println("Расстановка подтверждена!");
                break;
            } else {
                System.out.println("Неправильная команда!");
            }
            System.out.println("Расстановка первого игрока: ");
            System.out.println(player1.getPlayerStatus());
        }
        System.out.print("Имя второго игрока: ");
        Player player2 = new Player(scanner.nextLine(), sizeF);
        System.out.println("Расстановка второго игрока:");
        System.out.println(player2.getPlayerStatus());
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player2.genUnits(6);
            } else if (input.equals("2")) {
                if (player2.getCountLiveUnits() < 1) {
                    System.out.println("Невозможно продолжить, так как не было расстановки!");
                    continue;
                }
                System.out.println("Расстановка подтверждена!");
                break;
            } else {
                System.out.println("Неправильная команда!");
            }
            System.out.println("Расстановка второго игрока:");
            System.out.println(player2.getPlayerStatus());
        }
        System.out.println("Итоговая расстановка: ");
        System.out.println(getPlayersStatus(player1.getPlayerStatus(), player2.getPlayerStatus()));

        // Фаза боя
        System.out.println("Фаза игры: бой");
        System.out.println("""
                Команды:\n '0 n w' - передвинуть корабль, где n - номер корабля из списка начина с 1, w - направление(1 - влево или вверх, 2 - вправо или вниз)
                 '1 x y' - атаковать ракетой
                 '2 x y' - атаковать торпедой""");
        // Прописание ходов
        while (player1.getCountLiveUnits() > 0 && player2.getCountLiveUnits() > 0) {
            // Первый игрок
            while (true) {
                System.out.print("Ход первого игрока: ");
                String inp = scanner.nextLine();
                int[] checkCommand = new int[3];
                // Проверка команды
                if (!checkCommand(inp, checkCommand)) {
                    System.out.println("Неправильный ввод команды!");
                    continue;
                }
                if (checkCommand[0] == 0) {
                    // Проверки на номер корабля и направление
                    if (checkCommand[1] < 0 || checkCommand[1] > player1.getUnits().size()) {
                        System.out.println("Неправильный ввод номера корабля!");
                        continue;
                    }
                    if (checkCommand[2] != 1 && checkCommand[2] != 2) {
                        System.out.println("Неправильный ввод направления!");
                        continue;
                    }
                    if (player1.moveUnit(checkCommand[1], checkCommand[2])) {
                        System.out.println("Первый игрок успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1.getPlayerStatus(), player2.getPlayerStatus()));
                        break;
                    }
                    System.out.println("Невозможно передвинуть данный корабль!");

                } else if (checkCommand[0] == 1) {
                    // Проверка ввода координат
                    if (checkCommand[1] < 1 || checkCommand[1] > player1.getBattleField().getSize() || checkCommand[2] < 1 || checkCommand[2] > player1.getBattleField().getSize()) {
                        System.out.println("Неправильный ввод координат!");
                        continue;
                    }
                    // Проверка попадания
                    if (player1.attackByRocket(checkCommand[1], checkCommand[2], player2)) {
                        System.out.println("Первый игрок попал!");
                        System.out.println(getPlayersStatus(player1.getPlayerStatus(), player2.getPlayerStatus()));
                        if (player2.getCountLiveUnits() < 1) {
                            break;
                        }
                        continue;
                    }
                    System.out.println("Первый игрок промахнулся!");
                    System.out.println(getPlayersStatus(player1.getPlayerStatus(), player2.getPlayerStatus()));
                    break;
                } else if (checkCommand[0] == 2) {
                    break;
                } else {
                    System.out.println("Неправильный ввод команды!");
                }
            }
            // Проверка уничтожения игрока
            if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                break;
            }
            // Ход второго
            while (true) {
                System.out.print("Ход второго игрока: ");
                String inp = scanner.nextLine();
                int[] checkCommand = new int[3];
                // Проверка команды
                if (!checkCommand(inp, checkCommand)) {
                    continue;
                }
                if (checkCommand[0] == 0) {
                    // Проверки на номер корабля и направление
                    if (checkCommand[1] < 0 || checkCommand[1] > player1.getUnits().size()) {
                        System.out.println("Неправильный ввод номера корабля!");
                        continue;
                    }
                    if (checkCommand[2] != 1 && checkCommand[2] != 2) {
                        System.out.println("Неправильный ввод направления!");
                        continue;
                    }
                    if (player1.moveUnit(checkCommand[1], checkCommand[2])) {
                        System.out.println("Второй игрок успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1.getPlayerStatus(), player2.getPlayerStatus()));
                        break;
                    }
                    System.out.println("Невозможно передвинуть данный корабль!");
                } else if (checkCommand[0] == 1) {
                    if (checkCommand[1] < 1 || checkCommand[1] > player2.getBattleField().getSize() || checkCommand[2] < 1 || checkCommand[2] > player2.getBattleField().getSize()) {
                        System.out.println("Неправильный ввод координат!");
                        continue;
                    }
                    if (player2.attackByRocket(checkCommand[1], checkCommand[2], player1)) {
                        System.out.println("Второй игрок попал!");
                        System.out.println(getPlayersStatus(player1.getPlayerStatus(), player2.getPlayerStatus()));
                        if (player1.getCountLiveUnits() < 1) {
                            break;
                        }
                        continue;
                    }
                    System.out.println("Второй игрок промахнулся!");
                    System.out.println(getPlayersStatus(player1.getPlayerStatus(), player2.getPlayerStatus()));
                    break;
                } else if (checkCommand[0] == 2) {
                    break;
                } else {
                    System.out.println("Неправильный ввод команды!");
                }
            }
            // Проверка уничтожения игрока
            if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                break;
            }
        }
        // Итог
        System.out.println("Итог:");
        System.out.println(getPlayersStatus(player1.getPlayerStatus(), player2.getPlayerStatus()));
        if (player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() < 1) {
            System.out.println("Ничья!");
        } else if(player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() >= 1) {
            System.out.println("Победил второй игрок!");
        } else {
            System.out.println("Победил первый игрок!");
        }

    }

    // Получить поля игроков в нормальном виде
    public String getPlayersStatus(String player1Info, String player2Info) {
        StringBuilder str = new StringBuilder();
        String[] inp1 = player1Info.split("\n");
        String[] inp2 = player2Info.split("\n");
        for (int i = 0; i < Math.max(inp1.length, inp2.length); i++) {
            str.append(inp1[i]).append("       ").append(inp2[i]).append("\n");
        }
        return str.toString();
    }

    // Проверка команды на ход
    public boolean checkCommand(String input, int[] output) {
        String[] inp = input.split(" ");
        // Проверка команды на количество
        if (inp.length != 3) {
            return false;
        }
        // Проверка на цифры
        try {
            for (int i = 0; i < inp.length; i++) {
                output[i] = Integer.parseInt(inp[i]);
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Неправильный ввод команды!");
            return false;
        }
        return true;
    }
}
