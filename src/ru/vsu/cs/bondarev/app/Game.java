package ru.vsu.cs.bondarev.app;

import ru.vsu.cs.bondarev.units.Unit;

import java.util.Scanner;
import java.util.SortedMap;

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
        System.out.println(player1.getPlayerStatus(0, false));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player1.genUnits(cntUnit);
                System.out.println("Расстановка первого игрока: ");
                System.out.println(player1.getPlayerStatus(0, false));
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
        System.out.println(player2.getPlayerStatus(0, false));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player2.genUnits(cntUnit);
                System.out.println("Расстановка второго игрока:");
                System.out.println(player2.getPlayerStatus(0, false));
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
        System.out.println(getPlayersStatus(player1, player2, false));

        // Фаза боя
        System.out.println("Фаза игры: бой");
        // Прописание ходов
        int attackTorpedoP1 = 0;
        int attackTorpedoP1Y = 0;
        Unit atTor1 = null;
        int attackTorpedoP2Y = 0;
        int attackTorpedoP2 = 0;
        Unit atTor2 = null;
        while (player1.getCountLiveUnits() > 0 && player2.getCountLiveUnits() > 0) {
            // Первый игрок
            while (true) {
                if (attackTorpedoP1 == 1) {
                    attackTorpedoP1 = 0;
                    if (atTor1.attackByTorpedo(player1, player2, attackTorpedoP1Y)) {
                        System.out.println("Торпеда первого игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная первым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.println("Ход первого игрока... ");
                System.out.print("Введите номер корабля: ");
                int nUnit = readNUnit(player1, scanner, false);
                if (nUnit == -1) {
                    System.out.println("Ошибка! Попробуйте снова!");
                    continue;
                }
                Unit unitEdit = player1.getUnits().get(nUnit - 1);
                System.out.print("Команда 0 - передвинуть, 1 - атаковать ракетой, 2 - атаковать торпедой: ");
                int action = readAction(scanner, false);
                if (action == 0) {
                    System.out.print("Введите координаты для перемещения: ");
                    int[] point = readPoint(player1, scanner, false);
                    if (point[0] == -1) {
                        System.out.println("Неверно введены координаты!");
                        continue;
                    }
                    if (unitEdit.move(point[0] - 1, point[1] - 1, player1, player2)) {
                        System.out.println("Игрок успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    } else {
                        System.out.println("Не удалось передвинуть!");
                    }
                } else if (action == 1) {
                    if (!unitEdit.canAttackByRocket()) {
                        continue;
                    }
                    System.out.print("Введите координаты для атаки: ");
                    int[] point = readPoint(player2, scanner, false);
                    if (point[0] == -1) {
                        System.out.println("Неверно введены координаты!");
                        continue;
                    }
                    if (unitEdit.attackByRocket(point[0] - 1, point[1] - 1, player1, player2)) {
                        System.out.println("Игрок попал!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                    } else {
                        System.out.println("Игрок промахнулся!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    }
                } else if (action == 2) {
                    if (!unitEdit.canAttackByTor()) {
                        continue;
                    }
                    System.out.print("Введите y для запуска торпеды: ");
                    int y = readYPoint(player1, scanner, false);
                    if (y == -1) {
                        System.out.println("Неверно введена координата!");
                        continue;
                    }
                    if (unitEdit.canAttackTorpedo(y - 1)) {
                        System.out.println("Торпеда была успешно выпущена!");
                        attackTorpedoP1 = 1;
                        attackTorpedoP1Y = y - 1;
                        atTor1 = unitEdit;
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    } else {
                        System.out.println("Неудача! Торпеда не была выпущена!");
                    }
                }
                else {
                    System.out.println("Ошибка! Попробуйте снова!");
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
                    if (atTor2.attackByTorpedo(player2, player1, attackTorpedoP2Y)) {
                        System.out.println("Торпеда второго игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная второго игроком не попала во вражеского юнита!");
                    }
                }
                System.out.println("Ход второго игрока... ");
                System.out.print("Введите номер корабля: ");
                int nUnit = readNUnit(player2, scanner, false);
                if (nUnit == -1) {
                    System.out.println("Ошибка! Попробуйте снова!");
                    continue;
                }
                Unit unitEdit = player2.getUnits().get(nUnit - 1);
                System.out.print("Команда 0 - передвинуть, 1 - атаковать ракетой, 2 - атаковать торпедой: ");
                int action = readAction(scanner, false);
                if (action == 0) {
                    System.out.print("Введите координаты для перемещения: ");
                    int[] point = readPoint(player2, scanner, false);
                    if (point[0] == -1) {
                        System.out.println("Неверно введены координаты!");
                        continue;
                    }
                    if (unitEdit.move(point[0] - 1, point[1] - 1, player2, player1)) {
                        System.out.println("Игрок успешно передвинул корабль!");
                        break;
                    } else {
                        System.out.println("Не удалось передвинуть!");
                    }
                } else if (action == 1) {
                    if (!unitEdit.canAttackByRocket()) {
                        continue;
                    }
                    System.out.print("Введите координаты для атаки: ");
                    int[] point = readPoint(player1, scanner, false);
                    if (point[0] == -1) {
                        System.out.println("Неверно введены координаты!");
                        continue;
                    }
                    if (unitEdit.attackByRocket(point[0] - 1, point[1] - 1, player2, player1)) {
                        System.out.println("Игрок попал!");
                    } else {
                        System.out.println("Игрок промахнулся!");
                        break;
                    }
                } else if (action == 2) {
                    if (!unitEdit.canAttackByTor()) {
                        continue;
                    }
                    System.out.print("Введите y для запуска торпеды: ");
                    int y = readYPoint(player2, scanner, false);
                    if (y == -1) {
                        System.out.println("Неверно введена координата!");
                        continue;
                    }
                    if (unitEdit.canAttackTorpedo(y - 1)) {
                        System.out.println("Торпеда была успешно выпущена!");
                        attackTorpedoP2 = 1;
                        attackTorpedoP2Y = y - 1;
                        atTor2 = unitEdit;
                        break;
                    } else {
                        System.out.println("Неудача! Торпеда не была выпущена!");
                    }
                }
                else {
                    System.out.println("Ошибка! Попробуйте снова!");
                }
            }
            // Проверка уничтожения игрока
            if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                break;
            }
        }
        // Итог
        System.out.println("Итог:");
        System.out.println(getPlayersStatus(player1, player2, false));
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
        System.out.println(player1.getPlayerStatus(0, false));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player1.genUnits(cntUnit);
                System.out.println("Расстановка первого игрока: ");
                System.out.println(player1.getPlayerStatus(0, false));
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
        System.out.println(getPlayersStatus(player1, player2, true));

        // Фаза боя
        System.out.println("Фаза игры: бой");
        // Прописание ходов
        int attackTorpedoP1 = 0;
        int attackTorpedoP1Y = 0;
        Unit atTor1 = null;
        int attackTorpedoP2Y = 0;
        int attackTorpedoP2 = 0;
        Unit atTor2 = null;
        while (player1.getCountLiveUnits() > 0 && player2.getCountLiveUnits() > 0) {
            // Первый игрок
            while (true) {
                if (attackTorpedoP1 == 1) {
                    attackTorpedoP1 = 0;
                    if (atTor1.attackByTorpedo(player1, player2, attackTorpedoP1Y)) {
                        System.out.println("Торпеда первого игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2, true));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная первым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.println("Ход первого игрока... ");
                System.out.print("Введите номер корабля: ");
                int nUnit = readNUnit(player1, scanner, false);
                if (nUnit == -1) {
                    System.out.println("Ошибка! Попробуйте снова!");
                    continue;
                }
                Unit unitEdit = player1.getUnits().get(nUnit - 1);
                System.out.print("Команда 0 - передвинуть, 1 - атаковать ракетой, 2 - атаковать торпедой: ");
                int action = readAction(scanner, false);
                if (action == 0) {
                    System.out.print("Введите координаты для перемещения: ");
                    int[] point = readPoint(player1, scanner, false);
                    if (point[0] == -1) {
                        System.out.println("Неверно введены координаты!");
                        continue;
                    }
                    if (unitEdit.move(point[0] - 1, point[1] - 1, player1, player2)) {
                        System.out.println("Игрок успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2, true));
                        break;
                    } else {
                        System.out.println("Не удалось передвинуть!");
                    }
                } else if (action == 1) {
                    if (!unitEdit.canAttackByRocket()) {
                        continue;
                    }
                    System.out.print("Введите координаты для атаки: ");
                    int[] point = readPoint(player2, scanner, false);
                    if (point[0] == -1) {
                        System.out.println("Неверно введены координаты!");
                        continue;
                    }
                    if (unitEdit.attackByRocket(point[0] - 1, point[1] - 1, player1, player2)) {
                        System.out.println("Игрок попал!");
                        System.out.println(getPlayersStatus(player1, player2, true));
                    } else {
                        System.out.println("Игрок промахнулся!");
                        System.out.println(getPlayersStatus(player1, player2, true));
                        break;
                    }
                } else if (action == 2) {
                    if (!unitEdit.canAttackByTor()) {
                        continue;
                    }
                    System.out.print("Введите y для запуска торпеды: ");
                    int y = readYPoint(player1, scanner, false);
                    if (y == -1) {
                        System.out.println("Неверно введена координата!");
                        continue;
                    }
                    if (unitEdit.canAttackTorpedo(y - 1)) {
                        System.out.println("Торпеда была успешно выпущена!");
                        attackTorpedoP1 = 1;
                        attackTorpedoP1Y = y - 1;
                        atTor1 = unitEdit;
                        System.out.println(getPlayersStatus(player1, player2, true));
                        break;
                    } else {
                        System.out.println("Неудача! Торпеда не была выпущена!");
                    }
                }
                else {
                    System.out.println("Ошибка! Попробуйте снова!");
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
                    if (atTor2.attackByTorpedo(player2, player1, attackTorpedoP2Y)) {
                        System.out.println("Торпеда второго игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2, true));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная второго игроком не попала во вражеского юнита!");
                    }
                }
                System.out.println("Попытка хода бота... ");
                int nUnit = readNUnit(player2, scanner, true);
                if (nUnit == -1) {
                    System.out.println("Неудача!");
                    continue;
                }
                Unit unitEdit = player2.getUnits().get(nUnit - 1);
                int action = readAction(scanner, true);
                if (action == 0) {
                    int[] point = readPoint(player2, scanner, true);
                    if (point[0] == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.move(point[0] - 1, point[1] - 1, player2, player1)) {
                        System.out.println("Бот успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2, true));
                        break;
                    } else {
                        System.out.println("Неудача! Бот не смог передвинуть корабль!");
                    }
                } else if (action == 1) {
                    if (!unitEdit.canAttackByRocket()) {
                        continue;
                    }
                    int[] point = readPoint(player1, scanner, true);
                    if (point[0] == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.attackByRocket(point[0] - 1, point[1] - 1, player2, player1)) {
                        System.out.println("Бот попал!");
                        System.out.println(getPlayersStatus(player1, player2, true));
                    } else {
                        System.out.println("Игрок промахнулся!");
                        System.out.println(getPlayersStatus(player1, player2, true));
                        break;
                    }
                } else if (action == 2) {
                    if (!unitEdit.canAttackByTor()) {
                        continue;
                    }
                    int y = readYPoint(player2, scanner, true);
                    if (y == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.canAttackTorpedo(y - 1)) {
                        System.out.println("Торпеда была успешно выпущена!");
                        attackTorpedoP2 = 1;
                        attackTorpedoP2Y = y - 1;
                        atTor2 = unitEdit;
                        System.out.println(getPlayersStatus(player1, player2, true));
                        break;
                    } else {
                        System.out.println("Неудача! Торпеда не была выпущена!");
                    }
                }
                else {
                    System.out.println("Неудача!");
                }
            }
            // Проверка уничтожения игрока
            if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                break;
            }
        }
        // Итог
        System.out.println("Итог:");
        System.out.println(getPlayersStatus(player1, player2, false));
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
        System.out.println(player1.getPlayerStatus(0, false));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player1.genUnits(cntUnit);
                System.out.println("Расстановка первого бота: ");
                System.out.println(player1.getPlayerStatus(0, false));
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
        System.out.println(player2.getPlayerStatus(0, false));
        while (true) {
            System.out.print("Команда: ");
            input = scanner.nextLine();
            if (input.equals("1")) {
                player2.genUnits(cntUnit);
                System.out.println("Расстановка второго бота:");
                System.out.println(player2.getPlayerStatus(0, false));
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
        System.out.println(getPlayersStatus(player1, player2, false));

        // Фаза боя
        System.out.println("Фаза игры: бой");
        // Прописание ходов
        int attackTorpedoP1 = 0;
        int attackTorpedoP1Y = 0;
        Unit atTor1 = null;
        int attackTorpedoP2Y = 0;
        int attackTorpedoP2 = 0;
        Unit atTor2 = null;
        while (player1.getCountLiveUnits() > 0 && player2.getCountLiveUnits() > 0) {
            // Первый игрок
            while (true) {
                if (attackTorpedoP1 == 1) {
                    attackTorpedoP1 = 0;
                    if (atTor1.attackByTorpedo(player1, player2, attackTorpedoP1Y)) {
                        System.out.println("Торпеда первого игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная первым игроком не попала во вражеского юнита!");
                    }
                }
                System.out.println("Попытка хода первого бота... ");
                int nUnit = readNUnit(player1, scanner, true);
                if (nUnit == -1) {
                    System.out.println("Неудача!");
                    continue;
                }
                Unit unitEdit = player1.getUnits().get(nUnit - 1);
                int action = readAction(scanner, true);
                if (action == 0) {
                    int[] point = readPoint(player1, scanner, true);
                    if (point[0] == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.move(point[0] - 1, point[1] - 1, player1, player2)) {
                        System.out.println("Бот успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    } else {
                        System.out.println("Неудача! Бот не смог передвинуть корабль!");
                    }
                } else if (action == 1) {
                    if (!unitEdit.canAttackByRocket()) {
                        continue;
                    }
                    int[] point = readPoint(player1, scanner, true);
                    if (point[0] == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.attackByRocket(point[0] - 1, point[1] - 1, player1, player2)) {
                        System.out.println("Бот попал!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                    } else {
                        System.out.println("Игрок промахнулся!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    }
                } else if (action == 2) {
                    if (!unitEdit.canAttackByTor()) {
                        continue;
                    }
                    int y = readYPoint(player1, scanner, true);
                    if (y == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.canAttackTorpedo(y - 1)) {
                        System.out.println("Торпеда была успешно выпущена!");
                        attackTorpedoP1 = 1;
                        attackTorpedoP1Y = y - 1;
                        atTor1 = unitEdit;
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    } else {
                        System.out.println("Неудача! Торпеда не была выпущена!");
                    }
                }
                else {
                    System.out.println("Неудача!");
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
                    if (atTor2.attackByTorpedo(player2, player1, attackTorpedoP2Y)) {
                        System.out.println("Торпеда второго игрока уничтожила один юнит вражеского игрока!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                            break;
                        }
                    } else {
                        System.out.println("Торпеда, выпущенная второго игроком не попала во вражеского юнита!");
                    }
                }
                System.out.println("Попытка хода второго бота... ");
                int nUnit = readNUnit(player2, scanner, true);
                if (nUnit == -1) {
                    System.out.println("Неудача!");
                    continue;
                }
                Unit unitEdit = player2.getUnits().get(nUnit - 1);
                int action = readAction(scanner, true);
                if (action == 0) {
                    int[] point = readPoint(player2, scanner, true);
                    if (point[0] == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.move(point[0] - 1, point[1] - 1, player2, player1)) {
                        System.out.println("Бот успешно передвинул корабль!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    } else {
                        System.out.println("Неудача! Бот не смог передвинуть корабль!");
                    }
                } else if (action == 1) {
                    if (!unitEdit.canAttackByRocket()) {
                        continue;
                    }
                    int[] point = readPoint(player1, scanner, true);
                    if (point[0] == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.attackByRocket(point[0] - 1, point[1] - 1, player2, player1)) {
                        System.out.println("Бот попал!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                    } else {
                        System.out.println("Игрок промахнулся!");
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    }
                } else if (action == 2) {
                    if (!unitEdit.canAttackByTor()) {
                        continue;
                    }
                    int y = readYPoint(player2, scanner, true);
                    if (y == -1) {
                        System.out.println("Неудача!");
                        continue;
                    }
                    if (unitEdit.canAttackTorpedo(y - 1)) {
                        System.out.println("Торпеда была успешно выпущена!");
                        attackTorpedoP2 = 1;
                        attackTorpedoP2Y = y - 1;
                        atTor2 = unitEdit;
                        System.out.println(getPlayersStatus(player1, player2, false));
                        break;
                    } else {
                        System.out.println("Неудача! Торпеда не была выпущена!");
                    }
                }
                else {
                    System.out.println("Неудача!");
                }
            }

            // Проверка уничтожения игрока
            if (player1.getCountLiveUnits() < 1 || player2.getCountLiveUnits() < 1) {
                break;
            }
        }
        // Итог
        System.out.println("Итог:");
        System.out.println(getPlayersStatus(player1, player2, false));
        if (player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() < 1) {
            System.out.println("Ничья!");
        } else if(player1.getCountLiveUnits() < 1 && player2.getCountLiveUnits() >= 1) {
            System.out.println("Победил второй игрок!");
        } else {
            System.out.println("Победил первый игрок!");
        }
    }

    // Получить поля игроков в нормальном виде
    public String getPlayersStatus(Player player1, Player player2, boolean hide) {
        String player1Info;
        String player2Info;
        if (player1.getUnits().size() > player2.getUnits().size()) {
            player1Info = player1.getPlayerStatus(0, false);
            player2Info = player2.getPlayerStatus(player1.getUnits().size() - player2.getUnits().size(), hide);
        } else {
            player1Info = player1.getPlayerStatus(player2.getUnits().size() - player1.getUnits().size(), false);
            player2Info = player2.getPlayerStatus(0, hide);
        }
        StringBuilder str = new StringBuilder();
        String[] inp1 = player1Info.split("\n");
        String[] inp2 = player2Info.split("\n");
        for (int i = 0; i < Math.max(inp1.length, inp2.length); i++) {
            str.append(inp1[i]).append("       ").append(inp2[i]).append("\n");
        }
        return str.toString();
    }

    public int readAction(Scanner scanner, boolean bot) {
        int n = -1;
        if (!bot) {
            try {
                n = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
                return -1;
            }
            if (n > 2 || n < 0) {
                System.out.println("Неправильная команда!");
                return -1;
            }
        } else {
            n = (int) (Math.random() * 3);
        }
        return n;
    }

    public int readNUnit(Player player, Scanner scanner, boolean bot) {
        int n = -1;
        if (!bot) {
            try {
                n = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
                return -1;
            }
            if (n > player.getUnits().size() || n < 1) {
                System.out.println("Нет такого юнита в списке!");
                return -1;
            }
        } else {
            n = (int) (Math.random() * (player.getUnits().size() - 1) + 1);
        }
        return n;
    }

    public int[] readPoint(Player player, Scanner scanner, boolean bot) {
        int[] output = new int[]{-1, -1};
        if (!bot) {
            String[] inp = scanner.nextLine().split(" ");
            if (inp.length != 2) {
                System.out.println("Неправильно введены координаты!");
                return output;
            }
            // Проверка на цифры
            try {
                for (int i = 0; i < inp.length; i++) {
                    output[i] = Integer.parseInt(inp[i]);
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
                return new int[]{-1, -1};
            }
        } else {
            output[0] = (int) (Math.random() * (player.getBattleField().getSize()) + 1);
            output[1] = (int) (Math.random() * (player.getBattleField().getSize()) + 1);
        }
        return output;
    }

    public int readYPoint(Player player, Scanner scanner, boolean bot) {
        int y = -1;
        if (!bot) {
            System.out.print("Введите (y) для атаки торпедой: ");
            try {
                y = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException nfe) {
                System.out.println("Неправильный ввод команды!");
                return -1;
            }
        } else {
            y = (int) (Math.random() * (player.getBattleField().getSize() - 1) + 1);
        }
        return y;
    }
}
