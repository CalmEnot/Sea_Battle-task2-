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
        } else if (input.equals("3")) {
            gameMode3(scanner);
        } else if (input.equals("2")) {
            gameMode2(scanner);
        } else {
            System.out.println("Неправильный ввод! Завершение программы...");
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
                System.out.print("Размеры поля (число, не меньшее 10 и не большее 50): ");
                sizeF = Integer.parseInt(scanner.nextLine());
                if (sizeF < 10 || sizeF > 50) {
                    System.out.println("Недопустимые размеры поля!");
                    continue;
                }
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
            }
        }

        int cntUnit;
        //Выяснение количества юнитов
        while (true) {
            try {
                System.out.print("Количество юнитов (число, не меньшее 7 и не большее 15): ");
                cntUnit = Integer.parseInt(scanner.nextLine());
                if (cntUnit < 7 || cntUnit > 15) {
                    System.out.println("Недопустимое количество юнитов!");
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
        System.out.println(player1.getPlayerStatus(0));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player1.genUnits(cntUnit);
                System.out.println("Расстановка первого игрока: ");
                System.out.println(player1.getPlayerStatus(0));
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
        }
        System.out.print("Имя второго игрока: ");
        Player player2 = new Player(scanner.nextLine(), sizeF);
        System.out.println("Расстановка второго игрока:");
        System.out.println(player2.getPlayerStatus(0));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player2.genUnits(cntUnit);
                System.out.println("Расстановка второго игрока:");
                System.out.println(player2.getPlayerStatus(0));
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
        }
        System.out.println("Итоговая расстановка: ");
        System.out.println(getPlayersStatus(player1, player2));

        // Фаза боя
        System.out.println("Фаза игры: бой");
        System.out.println("""
                Команды:\n '0 n' - передвинуть корабль или тральщик или подводную лодку, где n - номер корабля из списка начина с 1
                 '1 x y' - атаковать ракетой
                 '2 y' - атаковать торпедой""");
        // Прописание ходов
        int attackTorpedoP1 = 0;
        int attackTorpedoP1Y = 0;
        int attackTorpedoP2Y = 0;
        int attackTorpedoP2 = 0;
        while (player1.getCountLiveUnits() > 0 && player2.getCountLiveUnits() > 0) {
            // Первый игрок
            while (true) {
                if (attackTorpedoP1 == 1) {
                    attackTorpedoP1 = 0;
                    if (player1.attackTorpedo(attackTorpedoP1Y, player2)) {
                        System.out.println("Торпеда первого игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная первым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.print("Ход первого игрока: ");
                String inp = scanner.nextLine();
                int[] checkCommand = new int[3];
                // Проверка команды
                if (!checkCommand(inp, checkCommand)) {
                    System.out.println("Неправильный ввод команды!");
                    continue;
                }
                if (checkCommand[0] == 0) {
                    // Проверки на номер корабля
                    if (checkCommand[1] < 1 || checkCommand[1] > player1.getUnits().size()) {
                        System.out.println("Неправильный ввод номера корабля!");
                        continue;
                    }
                    if (player1.getUnits().get(checkCommand[1] - 1).getSign().equals("[*]")) {
                        System.out.println("Невозможно передвинуть мину!");
                        continue;
                    }
                    // Проверка, на передвижение спец. корабля
                    if (player1.getUnits().get(checkCommand[1] - 1).getSign().equals("[$]") || player1.getUnits().get(checkCommand[1] - 1).getSign().equals("[#]")) {
                        System.out.println("На какие координаты (x, y) вы хотите передвинуть данный спец. корабль?");
                        int[] point = new int[2];
                        String pointStr;
                        while (true) {
                            System.out.print("Координаты: ");
                            pointStr = scanner.nextLine();
                            if (!checkCommand(pointStr, point)) {
                                System.out.println("Неправильный ввод координат!");
                                continue;
                            }
                            break;
                        }
                        if (player1.moveSpecUnit(checkCommand[1], point[0] - 1, point[1] - 1, player2)) {
                            System.out.println("Первый игрок успешно передвинул корабль!");
                            System.out.println(getPlayersStatus(player1, player2));
                            break;
                        }
                        System.out.println("Невозможно передвинуть спец. корабль!");
                        continue;
                    }
                    System.out.println("В каком направлении вы хотите передвинуть корабль?(1 - влево или вверх, 2 - вправо или вниз)");
                    int way;
                    while (true) {
                        System.out.print("Направление: ");
                        try {
                            way = Integer.parseInt(scanner.nextLine());
                            if (way != 1 && way != 2) {
                                System.out.println("Неправильный ввод направления!");
                                continue;
                            }
                        } catch (NumberFormatException nfe) {
                            System.out.println("Неправильный ввод направления!");
                            continue;
                        }
                        break;
                    }
                    if (player1.moveBattleShip(checkCommand[1], way)) {
                        System.out.println("Первый игрок успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2));
                        break;
                    }
                    System.out.println("Невозможно передвинуть данный объект!");

                } else if (checkCommand[0] == 1) {
                    // Проверка ввода координат
                    if (checkCommand[1] < 1 || checkCommand[1] > player1.getBattleField().getSize() || checkCommand[2] < 1 || checkCommand[2] > player1.getBattleField().getSize()) {
                        System.out.println("Неправильный ввод координат!");
                        continue;
                    }
                    // Проверка попадания
                    if (player1.attackByRocket(checkCommand[1], checkCommand[2], player2)) {
                        System.out.println("Первый игрок попал!");
                        System.out.println(getPlayersStatus(player1, player2));
                        if (player2.getCountLiveUnits() < 1) {
                            break;
                        }
                        continue;
                    }
                    System.out.println("Первый игрок промахнулся!");
                    System.out.println(getPlayersStatus(player1, player2));
                    break;
                }
                // Атака торпедой
                else if (checkCommand[0] == 2) {
                    if (checkCommand[1] < 1 || checkCommand[1] > player1.getBattleField().getSize()) {
                        System.out.println("Неправильный ввод координаты!");
                        continue;
                    }
                    // Проверка, есть ли корабли по y
                    if (!player1.canAttackByTorpedo(checkCommand[1] - 1)) {
                        System.out.println("Нет кораблей, чтобы можно было атаковать по данной координате!");
                        continue;
                    }
                    System.out.println("Игрок выпустил торпеду!");
                    attackTorpedoP1 = 1;
                    attackTorpedoP1Y = checkCommand[1] - 1;
                    System.out.println(getPlayersStatus(player1, player2));
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
                if (attackTorpedoP2 == 1) {
                    attackTorpedoP2 = 0;
                    if (player2.attackTorpedo(attackTorpedoP2Y, player1)) {
                        System.out.println("Торпеда второго игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная вторым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.print("Ход второго игрока: ");
                String inp = scanner.nextLine();
                int[] checkCommand = new int[3];
                // Проверка команды
                if (!checkCommand(inp, checkCommand)) {
                    continue;
                }
                if (checkCommand[0] == 0) {
                    // Проверки на номер корабля
                    if (checkCommand[1] < 1 || checkCommand[1] > player2.getUnits().size()) {
                        System.out.println("Неправильный ввод номера корабля!");
                        continue;
                    }

                    if (player2.getUnits().get(checkCommand[1] - 1).getSign().equals("[*]")) {
                        System.out.println("Невозможно передвинуть мину!");
                        continue;
                    }
                    // Проверка, на передвижение спец. корабля
                    if (player2.getUnits().get(checkCommand[1] - 1).getSign().equals("[$]") || player2.getUnits().get(checkCommand[1] - 1).getSign().equals("[#]")) {
                        System.out.println("На какие координаты (x, y) вы хотите передвинуть данный спец. корабль?");
                        int[] point = new int[2];
                        String pointStr;
                        while (true) {
                            System.out.print("Координаты: ");
                            pointStr = scanner.nextLine();
                            if (!checkCommand(pointStr, point)) {
                                System.out.println("Неправильный ввод координат!");
                                continue;
                            }
                            break;
                        }
                        if (player2.moveSpecUnit(checkCommand[1], point[0] - 1, point[1] - 1, player1)) {
                            System.out.println("Второй игрок успешно передвинул корабль!");
                            System.out.println(getPlayersStatus(player1, player2));
                            break;
                        }
                        System.out.println("Невозможно передвинуть спец. корабль!");
                        continue;
                    }
                    System.out.println("В каком направлении вы хотите передвинуть корабль?(1 - влево или вверх, 2 - вправо или вниз)");
                    int way;
                    while (true) {
                        System.out.print("Направление: ");
                        try {
                            way = Integer.parseInt(scanner.nextLine());
                            if (way != 1 && way != 2) {
                                System.out.println("Неправильный ввод направления!");
                                continue;
                            }
                        } catch (NumberFormatException nfe) {
                            System.out.println("Неправильный ввод направления!");
                            continue;
                        }
                        break;
                    }
                    if (player2.moveBattleShip(checkCommand[1], way)) {
                        System.out.println("Второй игрок успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2));
                        break;
                    }
                    System.out.println("Невозможно передвинуть данный объект!");
                } else if (checkCommand[0] == 1) {
                    if (checkCommand[1] < 1 || checkCommand[1] > player2.getBattleField().getSize() || checkCommand[2] < 1 || checkCommand[2] > player2.getBattleField().getSize()) {
                        System.out.println("Неправильный ввод координат!");
                        continue;
                    }
                    if (player2.attackByRocket(checkCommand[1], checkCommand[2], player1)) {
                        System.out.println("Второй игрок попал!");
                        System.out.println(getPlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1) {
                            break;
                        }
                        continue;
                    }
                    System.out.println("Второй игрок промахнулся!");
                    System.out.println(getPlayersStatus(player1, player2));
                    break;
                }
                // Атака торпедой
                else if (checkCommand[0] == 2) {
                    if (checkCommand[1] < 1 || checkCommand[1] > player2.getBattleField().getSize()) {
                        System.out.println("Неправильный ввод координаты!");
                        continue;
                    }
                    // Проверка, есть ли корабли по y
                    if (!player2.canAttackByTorpedo(checkCommand[1] - 1)) {
                        System.out.println("Нет кораблей, чтобы можно было атаковать по данной координате!");
                        continue;
                    }
                    System.out.println("Игрок выпустил торпеду!");
                    attackTorpedoP2 = 1;
                    attackTorpedoP2Y = checkCommand[1] - 1;
                    System.out.println(getPlayersStatus(player1, player2));
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
        System.out.println(getPlayersStatus(player1, player2));
        if (player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() < 1) {
            System.out.println("Ничья!");
        } else if(player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() >= 1) {
            System.out.println("Победил второй игрок!");
        } else {
            System.out.println("Победил первый игрок!");
        }
    }
    // Игра против компьютера
    private void gameMode2(Scanner scanner) {
        System.out.println("Выбран режим: игра против ии");
        System.out.println("Фаза игры: расстановка");
        // Фаза расстановки
        System.out.println("Команды: 1 - сгенерировать корабли, 2 - подтвердить расстановку");
        String input = "";
        int sizeF;
        // Выяснение размеров поля
        while (true) {
            try {
                System.out.print("Размеры поля (число, не меньшее 10 и не большее 50): ");
                sizeF = Integer.parseInt(scanner.nextLine());
                if (sizeF < 10 || sizeF > 50) {
                    System.out.println("Недопустимые размеры поля!");
                    continue;
                }
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
            }
        }

        int cntUnit;
        //Выяснение количества юнитов
        while (true) {
            try {
                System.out.print("Количество юнитов (число, не меньшее 7 и не большее 15): ");
                cntUnit = Integer.parseInt(scanner.nextLine());
                if (cntUnit < 7 || cntUnit > 15) {
                    System.out.println("Недопустимое количество юнитов!");
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
        System.out.println(player1.getPlayerStatus(0));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player1.genUnits(cntUnit);
                System.out.println("Расстановка первого игрока: ");
                System.out.println(player1.getPlayerStatus(0));
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
        }
        System.out.println("Имя второго игрока: Bot2");
        Player player2 = new Player("Bot2", sizeF);
        System.out.println("Расстановка второго игрока...");
        player2.genUnits(cntUnit);
        System.out.println("Итоговая расстановка: ");
        System.out.println(getHideP2PlayersStatus(player1, player2));

        // Фаза боя
        System.out.println("Фаза игры: бой");
        System.out.println("""
                Команды:\n '0 n' - передвинуть корабль или тральщик или подводную лодку, где n - номер корабля из списка начина с 1
                 '1 x y' - атаковать ракетой
                 '2 y' - атаковать торпедой""");
        // Прописание ходов
        int attackTorpedoP1 = 0;
        int attackTorpedoP1Y = 0;
        int attackTorpedoP2Y = 0;
        int attackTorpedoP2 = 0;
        while (player1.getCountLiveUnits() > 0 && player2.getCountLiveUnits() > 0) {
            // Первый игрок
            while (true) {
                if (attackTorpedoP1 == 1) {
                    attackTorpedoP1 = 0;
                    if (player1.attackTorpedo(attackTorpedoP1Y, player2)) {
                        System.out.println("Торпеда первого игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getHideP2PlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная первым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.print("Ход первого игрока: ");
                String inp = scanner.nextLine();
                int[] checkCommand = new int[3];
                // Проверка команды
                if (!checkCommand(inp, checkCommand)) {
                    System.out.println("Неправильный ввод команды!");
                    continue;
                }
                if (checkCommand[0] == 0) {
                    // Проверки на номер корабля
                    if (checkCommand[1] < 1 || checkCommand[1] > player1.getUnits().size()) {
                        System.out.println("Неправильный ввод номера корабля!");
                        continue;
                    }
                    if (player1.getUnits().get(checkCommand[1] - 1).getSign().equals("[*]")) {
                        System.out.println("Невозможно передвинуть мину!");
                        continue;
                    }
                    // Проверка, на передвижение спец. корабля
                    if (player1.getUnits().get(checkCommand[1] - 1).getSign().equals("[$]") || player1.getUnits().get(checkCommand[1] - 1).getSign().equals("[#]")) {
                        System.out.println("На какие координаты (x, y) вы хотите передвинуть данный спец. корабль?");
                        int[] point = new int[2];
                        String pointStr;
                        while (true) {
                            System.out.print("Координаты: ");
                            pointStr = scanner.nextLine();
                            if (!checkCommand(pointStr, point)) {
                                System.out.println("Неправильный ввод координат!");
                                continue;
                            }
                            break;
                        }
                        if (player1.moveSpecUnit(checkCommand[1], point[0] - 1, point[1] - 1, player2)) {
                            System.out.println("Первый игрок успешно передвинул корабль!");
                            System.out.println(getHideP2PlayersStatus(player1, player2));
                            break;
                        }
                        System.out.println("Невозможно передвинуть спец. корабль!");
                        continue;
                    }
                    System.out.println("В каком направлении вы хотите передвинуть корабль?(1 - влево или вверх, 2 - вправо или вниз)");
                    int way;
                    while (true) {
                        System.out.print("Направление: ");
                        try {
                            way = Integer.parseInt(scanner.nextLine());
                            if (way != 1 && way != 2) {
                                System.out.println("Неправильный ввод направления!");
                                continue;
                            }
                        } catch (NumberFormatException nfe) {
                            System.out.println("Неправильный ввод направления!");
                            continue;
                        }
                        break;
                    }
                    if (player1.moveBattleShip(checkCommand[1], way)) {
                        System.out.println("Первый игрок успешно передвинул корабль!");
                        System.out.println(getHideP2PlayersStatus(player1, player2));
                        break;
                    }
                    System.out.println("Невозможно передвинуть данный объект!");

                } else if (checkCommand[0] == 1) {
                    // Проверка ввода координат
                    if (checkCommand[1] < 1 || checkCommand[1] > player1.getBattleField().getSize() || checkCommand[2] < 1 || checkCommand[2] > player1.getBattleField().getSize()) {
                        System.out.println("Неправильный ввод координат!");
                        continue;
                    }
                    // Проверка попадания
                    if (player1.attackByRocket(checkCommand[1], checkCommand[2], player2)) {
                        System.out.println("Первый игрок попал!");
                        System.out.println(getHideP2PlayersStatus(player1, player2));
                        if (player2.getCountLiveUnits() < 1) {
                            break;
                        }
                        continue;
                    }
                    System.out.println("Первый игрок промахнулся!");
                    System.out.println(getHideP2PlayersStatus(player1, player2));
                    break;
                }
                // Атака торпедой
                else if (checkCommand[0] == 2) {
                    if (checkCommand[1] < 1 || checkCommand[1] > player1.getBattleField().getSize()) {
                        System.out.println("Неправильный ввод координаты!");
                        continue;
                    }
                    // Проверка, есть ли корабли по y
                    if (!player1.canAttackByTorpedo(checkCommand[1] - 1)) {
                        System.out.println("Нет кораблей, чтобы можно было атаковать по данной координате!");
                        continue;
                    }
                    System.out.println("Игрок выпустил торпеду!");
                    attackTorpedoP1 = 1;
                    attackTorpedoP1Y = checkCommand[1] - 1;
                    System.out.println(getHideP2PlayersStatus(player1, player2));
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
                if (attackTorpedoP2 == 1) {
                    attackTorpedoP2 = 0;
                    if (player2.attackTorpedo(attackTorpedoP2Y, player1)) {
                        System.out.println("Торпеда второго игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getHideP2PlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная первым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.print("Ход второго бота... ");
                int inp = (int) (Math.random() * 4);
                if (inp == 0) {
                    System.out.println("Попытка переместить юнит...");
                    int nUnit = (int) (Math.random() * (player2.getUnits().size() - 1) + 1);
                    // Проверки на номер корабля
                    if (player2.getUnits().get(nUnit - 1).getSign().equals("[*]")) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    // Проверка, на передвижение спец. корабля
                    if (player2.getUnits().get(nUnit).getSign().equals("[$]") || player2.getUnits().get(nUnit).getSign().equals("[#]")) {
                        int[] point = new int[] {(int) (Math.random() * player2.getBattleField().getSize()), (int) (Math.random() * player2.getBattleField().getSize())};
                        if (player2.moveSpecUnit(nUnit, point[0], point[1], player1)) {
                            System.out.println("Второй игрок успешно передвинул корабль!");
                            break;
                        }
                        System.out.println("Неудача!");
                        continue;
                    }
                    int way = (int) (Math.random() * 2 + 1);
                    if (player2.moveBattleShip(nUnit, way)) {
                        System.out.println("Второй игрок успешно передвинул корабль!");
                        break;
                    }
                    System.out.println("Неудача!");

                } else if (inp == 1) {
                    System.out.println("Попытка атаки ракетой");
                    int[] point = new int[] {(int) (Math.random() * player2.getBattleField().getSize()), (int) (Math.random() * player2.getBattleField().getSize())};
                    // Проверка попадания
                    if (player2.attackByRocket(point[0] + 1, point[1] + 1, player1)) {
                        System.out.println("Второй бот попал!");
                        System.out.println(getHideP2PlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1) {
                            break;
                        }
                        continue;
                    }
                    System.out.println("Неудача! Второй бот промахнулся!");
                    System.out.println(getHideP2PlayersStatus(player1, player2));
                    break;
                }
                // Атака торпедой
                else if (inp == 2) {
                    System.out.println("Попытка атаки торпедой...");
                    int ranY = (int) (Math.random() * player2.getBattleField().getSize());
                    // Проверка, есть ли корабли по y
                    if (!player2.canAttackByTorpedo(ranY)) {
                        System.out.println("Неудача! Нет кораблей, чтобы можно было атаковать по данной координате!");
                        continue;
                    }
                    System.out.println("Удача! Игрок выпустил торпеду!");
                    attackTorpedoP2 = 1;
                    attackTorpedoP2Y = ranY;
                    break;
                }
                System.out.println("Неудача!");
            }
            // Проверка уничтожения игрока
            if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                break;
            }
        }
        // Итог
        System.out.println("Итог:");
        System.out.println(getPlayersStatus(player1, player2));
        if (player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() < 1) {
            System.out.println("Ничья!");
        } else if(player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() >= 1) {
            System.out.println("Победил второй игрок!");
        } else {
            System.out.println("Победил первый игрок!");
        }
    }

    // Игра 2 ботов
    private void gameMode3(Scanner scanner) {
        System.out.println("Выбран режим: игра 2 ботов");
        System.out.println("Фаза игры: расстановка");
        // Фаза расстановки
        System.out.println("Команды: 1 - сгенерировать корабли, 2 - подтвердить расстановку");
        String input = "";
        int sizeF;
        // Выяснение размеров поля
        while (true) {
            try {
                System.out.print("Размеры поля (число, не меньшее 10 и не большее 50): ");
                sizeF = Integer.parseInt(scanner.nextLine());
                if (sizeF < 10 || sizeF > 50) {
                    System.out.println("Недопустимые размеры поля!");
                    continue;
                }
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
            }
        }

        int cntUnit;
        //Выяснение количества юнитов
        while (true) {
            try {
                System.out.print("Количество юнитов (число, не меньшее 7 и не большее 15): ");
                cntUnit = Integer.parseInt(scanner.nextLine());
                if (cntUnit < 7 || cntUnit > 15) {
                    System.out.println("Недопустимое количество юнитов!");
                    continue;
                }
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
            }
        }

        System.out.println("Имя первого Бота: Bot1");
        Player player1 = new Player("Bot1", sizeF);
        System.out.println("Расстановка первого Бота: ");
        System.out.println(player1.getPlayerStatus(0));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player1.genUnits(cntUnit);
                System.out.println("Расстановка первого бота: ");
                System.out.println(player1.getPlayerStatus(0));
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
        }
        System.out.println("Имя второго бота: Bot2");
        Player player2 = new Player("Bot2", sizeF);
        System.out.println("Расстановка второго бота: ");
        System.out.println(player2.getPlayerStatus(0));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player2.genUnits(cntUnit);
                System.out.println("Расстановка второго бота:");
                System.out.println(player2.getPlayerStatus(0));
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
        }
        System.out.println("Итоговая расстановка: ");
        System.out.println(getPlayersStatus(player1, player2));

        // Фаза боя
        System.out.println("Фаза игры: бой");
        System.out.println("""
                Команды:\n '0 n' - передвинуть корабль или тральщик или подводную лодку, где n - номер корабля из списка начина с 1
                 '1 x y' - атаковать ракетой
                 '2 y' - атаковать торпедой""");
        // Прописание ходов
        int attackTorpedoP1 = 0;
        int attackTorpedoP1Y = 0;
        int attackTorpedoP2Y = 0;
        int attackTorpedoP2 = 0;
        while (player1.getCountLiveUnits() > 0 && player2.getCountLiveUnits() > 0) {
            // Первый игрок
            while (true) {
                if (attackTorpedoP1 == 1) {
                    attackTorpedoP1 = 0;
                    if (player1.attackTorpedo(attackTorpedoP1Y, player2)) {
                        System.out.println("Торпеда первого игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная первым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.print("Ход первого бота... ");
                int inp = (int) (Math.random() * 4);
                if (inp == 0) {
                    System.out.println("Попытка переместить юнит...");
                    int nUnit = (int) (Math.random() * (player1.getUnits().size() - 1) + 1);
                    // Проверки на номер корабля
                    if (player1.getUnits().get(nUnit - 1).getSign().equals("[*]")) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    // Проверка, на передвижение спец. корабля
                    if (player1.getUnits().get(nUnit).getSign().equals("[$]") || player1.getUnits().get(nUnit).getSign().equals("[#]")) {
                        int[] point = new int[] {(int) (Math.random() * player1.getBattleField().getSize()), (int) (Math.random() * player1.getBattleField().getSize())};
                        if (player1.moveSpecUnit(nUnit, point[0], point[1], player2)) {
                            System.out.println("Первый игрок успешно передвинул корабль!");
                            System.out.println(getPlayersStatus(player1, player2));
                            break;
                        }
                        System.out.println("Неудача!");
                        continue;
                    }
                    int way = (int) (Math.random() * 2 + 1);
                    if (player1.moveBattleShip(nUnit, way)) {
                        System.out.println("Первый игрок успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2));
                        break;
                    }
                    System.out.println("Неудача!");

                } else if (inp == 1) {
                    System.out.println("Попытка атаки ракетой");
                    int[] point = new int[] {(int) (Math.random() * player1.getBattleField().getSize()), (int) (Math.random() * player1.getBattleField().getSize())};
                    // Проверка попадания
                    if (player1.attackByRocket(point[0] + 1, point[1] + 1, player2)) {
                        System.out.println("Первый бот попал!");
                        System.out.println(getPlayersStatus(player1, player2));
                        if (player2.getCountLiveUnits() < 1) {
                            break;
                        }
                        continue;
                    }
                    System.out.println("Неудача! Первый бот промахнулся!");
                    System.out.println(getPlayersStatus(player1, player2));
                    break;
                }
                // Атака торпедой
                else if (inp == 2) {
                    System.out.println("Попытка атаки торпедой...");
                    int ranY =(int) (Math.random() * player1.getBattleField().getSize());
                    // Проверка, есть ли корабли по y
                    if (!player1.canAttackByTorpedo(ranY)) {
                        System.out.println("Неудача! Нет кораблей, чтобы можно было атаковать по данной координате!");
                        continue;
                    }
                    System.out.println("Удача! Игрок выпустил торпеду!");
                    attackTorpedoP1 = 1;
                    attackTorpedoP1Y = ranY;
                    System.out.println(getPlayersStatus(player1, player2));
                    break;
                }
                System.out.println("Неудача!");
            }
            // Проверка уничтожения игрока
            if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                break;
            }
            // Ход второго
            while (true) {
                if (attackTorpedoP2 == 1) {
                    attackTorpedoP2 = 0;
                    if (player2.attackTorpedo(attackTorpedoP2Y, player1)) {
                        System.out.println("Торпеда второго игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная первым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.print("Ход второго бота... ");
                int inp = (int) (Math.random() * 4);
                if (inp == 0) {
                    System.out.println("Попытка переместить юнит...");
                    int nUnit = (int) (Math.random() * (player2.getUnits().size() - 1) + 1);
                    // Проверки на номер корабля
                    if (player2.getUnits().get(nUnit - 1).getSign().equals("[*]")) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    // Проверка, на передвижение спец. корабля
                    if (player2.getUnits().get(nUnit).getSign().equals("[$]") || player2.getUnits().get(nUnit).getSign().equals("[#]")) {
                        int[] point = new int[] {(int) (Math.random() * player2.getBattleField().getSize()), (int) (Math.random() * player2.getBattleField().getSize())};
                        if (player2.moveSpecUnit(nUnit, point[0], point[1], player1)) {
                            System.out.println("Второй игрок успешно передвинул корабль!");
                            System.out.println(getPlayersStatus(player1, player2));
                            break;
                        }
                        System.out.println("Неудача!");
                        continue;
                    }
                    int way = (int) (Math.random() * 2 + 1);
                    if (player2.moveBattleShip(nUnit, way)) {
                        System.out.println("Второй игрок успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2));
                        break;
                    }
                    System.out.println("Неудача!");

                } else if (inp == 1) {
                    System.out.println("Попытка атаки ракетой");
                    int[] point = new int[] {(int) (Math.random() * player2.getBattleField().getSize()), (int) (Math.random() * player2.getBattleField().getSize())};
                    // Проверка попадания
                    if (player2.attackByRocket(point[0] + 1, point[1] + 1, player1)) {
                        System.out.println("Второй бот попал!");
                        System.out.println(getPlayersStatus(player1, player2));
                        if (player1.getCountLiveUnits() < 1) {
                            break;
                        }
                        continue;
                    }
                    System.out.println("Неудача! Второй бот промахнулся!");
                    System.out.println(getPlayersStatus(player1, player2));
                    break;
                }
                // Атака торпедой
                else if (inp == 2) {
                    System.out.println("Попытка атаки торпедой...");
                    int ranY = (int) (Math.random() * player2.getBattleField().getSize());
                    // Проверка, есть ли корабли по y
                    if (!player2.canAttackByTorpedo(ranY)) {
                        System.out.println("Неудача! Нет кораблей, чтобы можно было атаковать по данной координате!");
                        continue;
                    }
                    System.out.println("Удача! Игрок выпустил торпеду!");
                    attackTorpedoP2 = 1;
                    attackTorpedoP2Y = ranY;
                    System.out.println(getPlayersStatus(player1, player2));
                    break;
                }
                System.out.println("Неудача!");
            }
            // Проверка уничтожения игрока
            if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                break;
            }
        }
        // Итог
        System.out.println("Итог:");
        System.out.println(getPlayersStatus(player1, player2));
        if (player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() < 1) {
            System.out.println("Ничья!");
        } else if(player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() >= 1) {
            System.out.println("Победил второй игрок!");
        } else {
            System.out.println("Победил первый игрок!");
        }
    }

    // Получить поля игроков в нормальном виде
    public String getPlayersStatus(Player player1, Player player2) {
        String player1Info;
        String player2Info;
        if (player1.getUnits().size() > player2.getUnits().size()) {
            player1Info = player1.getPlayerStatus(0);
            player2Info = player2.getPlayerStatus(player1.getUnits().size() - player2.getUnits().size());
        } else {
            player1Info = player1.getPlayerStatus(player2.getUnits().size() - player1.getUnits().size());
            player2Info = player2.getPlayerStatus(0);
        }
        StringBuilder str = new StringBuilder();
        String[] inp1 = player1Info.split("\n");
        String[] inp2 = player2Info.split("\n");
        for (int i = 0; i < Math.max(inp1.length, inp2.length); i++) {
            str.append(inp1[i]).append("       ").append(inp2[i]).append("\n");
        }
        return str.toString();
    }

    public String getHideP2PlayersStatus(Player player1, Player player2) {
        String player1Info;
        String player2Info;
        if (player1.getUnits().size() > player2.getUnits().size()) {
            player1Info = player1.getPlayerStatus(0);
            player2Info = player2.getHidePlayerStatus(player1.getUnits().size() - player2.getUnits().size());
        } else {
            player1Info = player1.getPlayerStatus(player2.getUnits().size() - player1.getUnits().size());
            player2Info = player2.getHidePlayerStatus(0);
        }
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
        if (inp.length > 3 || inp.length < 2) {
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
