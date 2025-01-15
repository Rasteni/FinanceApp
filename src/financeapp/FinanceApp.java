package financeapp;

import java.io.*;
import java.util.*;

public class FinanceApp {

    private static final String DATA_FILE = "finance_data.txt";
    private static Map<String, User> users = new HashMap<>();
    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        System.out.println("Добро пожаловать в приложение для управления личными финансами!");
        mainLoop();
        saveData();
        System.out.println("До свидания!");
    }

    private static void mainLoop() {
        while (true) {
            if (currentUser == null) {
                System.out.println("Выберите действие: (login/register/exit)");
                String action = scanner.nextLine().trim();
                switch (action) {
                    case "login":
                        login();
                        break;
                    case "register":
                        register();
                        break;
                    case "exit":
                        return;
                    default:
                        System.out.println("Некорректная команда. Пожалуйста, введите login/register/exit.");
                }
            } else {
                System.out.println("\nВыберите действие:");
                System.out.println("1. Добавить доход");
                System.out.println("2. Добавить расход");
                System.out.println("3. Установить бюджет");
                System.out.println("4. Вывести информацию");
                System.out.println("5. Выйти из аккаунта");
                System.out.println("6. Выйти из приложения");
                System.out.println("7. Перевод средств"); // Дополнительная функция
                System.out.print("Введите номер действия: ");

                String choiceStr = scanner.nextLine().trim();

                if (!choiceStr.matches("\\d+")) {
                    System.out.println("Некорректный ввод. Введите номер действия.");
                    continue;
                }

                int choice = Integer.parseInt(choiceStr);
                switch (choice) {
                    case 1:
                        addIncome();
                        break;
                    case 2:
                        addExpense();
                        break;
                    case 3:
                        setBudget();
                        break;
                    case 4:
                        displayInfo();
                        break;
                    case 5:
                        logout();
                        break;
                    case 6:
                        return;
                    case 7:
                        transferFunds(); // Дополнительная функция
                        break;
                    default:
                        System.out.println("Некорректный номер действия.");
                }
            }
        }
    }

    //==================== Авторизация ====================
    private static void register() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();
        if (users.containsKey(username)) {
            System.out.println("Пользователь с таким логином уже существует.");
            return;
        }
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Логин и пароль не могут быть пустыми.");
            return;
        }
        User newUser = new User(username, password);
        users.put(username, newUser);
        System.out.println("Пользователь успешно зарегистрирован.");
    }

    private static void login() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Вход выполнен успешно!");
        } else {
            System.out.println("Неверный логин или пароль.");
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Вы вышли из аккаунта.");
    }
    //==================== Управление финансами ====================
    private static void addIncome() {
        System.out.print("Введите категорию дохода: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println("Категория дохода не может быть пустой.");
            return;
        }
        System.out.print("Введите сумму дохода: ");
        String amountStr = scanner.nextLine().trim();

        if (!amountStr.matches("\\d+(\\.\\d+)?")) {
            System.out.println("Некорректный формат суммы дохода.");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        if (amount <= 0) {
            System.out.println("Сумма дохода должна быть больше нуля.");
            return;
        }
        currentUser.getWallet().addIncome(category, amount);
        System.out.println("Доход добавлен.");
    }


    private static void addExpense() {
        System.out.print("Введите категорию расхода: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println("Категория расхода не может быть пустой.");
            return;
        }
        System.out.print("Введите сумму расхода: ");
        String amountStr = scanner.nextLine().trim();

        if (!amountStr.matches("\\d+(\\.\\d+)?")) {
            System.out.println("Некорректный формат суммы расхода.");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        if (amount <= 0) {
            System.out.println("Сумма расхода должна быть больше нуля.");
            return;
        }

        currentUser.getWallet().addExpense(category, amount);
        checkBudget(category, amount);
        System.out.println("Расход добавлен.");
    }


    private static void setBudget() {
        System.out.print("Введите категорию бюджета: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println("Категория бюджета не может быть пустой.");
            return;
        }
        System.out.print("Введите сумму бюджета: ");
        String amountStr = scanner.nextLine().trim();

        if (!amountStr.matches("\\d+(\\.\\d+)?")) {
            System.out.println("Некорректный формат суммы бюджета.");
            return;
        }
        double amount = Double.parseDouble(amountStr);

        if (amount <= 0) {
            System.out.println("Сумма бюджета должна быть больше нуля.");
            return;
        }
        currentUser.getWallet().setBudget(category, amount);
        System.out.println("Бюджет установлен.");
    }

    //==================== Вывод информации ====================
    private static void displayInfo() {
        Wallet wallet = currentUser.getWallet();

        System.out.println("Общий доход: " + wallet.getTotalIncome());
        System.out.println("Доходы по категориям:");
        wallet.getIncomeByCategory().forEach((category, amount) -> System.out.println(category + ": " + amount));

        System.out.println("Общие расходы: " + wallet.getTotalExpense());
        System.out.println("Бюджет по категориям:");
        wallet.getBudgetByCategory().forEach((category, budget) -> {
            double spent = wallet.getExpenseByCategory().getOrDefault(category, 0.0);
            double remaining = budget - spent;
            System.out.println(category + ": " + budget + ", Оставшийся бюджет: " + remaining);
        });

        if (wallet.getTotalExpense() > wallet.getTotalIncome()) {
            System.out.println("Внимание: Расходы превышают доходы!");
        }

    }

    //==================== Подсчет расходов и доходов ====================
    private static double getTotalIncomeByCategory(List<String> categories) {
        Wallet wallet = currentUser.getWallet();
        double totalIncome = 0;
        for (String category : categories) {
            if (wallet.getIncomeByCategory().containsKey(category)) {
                totalIncome += wallet.getIncomeByCategory().get(category);
            } else {
                System.out.println("Категория дохода '" + category + "' не найдена.");
            }
        }
        return totalIncome;
    }

    private static double getTotalExpenseByCategory(List<String> categories) {
        Wallet wallet = currentUser.getWallet();
        double totalExpense = 0;
        for (String category : categories) {
            if (wallet.getExpenseByCategory().containsKey(category)) {
                totalExpense += wallet.getExpenseByCategory().get(category);
            } else {
                System.out.println("Категория расхода '" + category + "' не найдена.");
            }
        }
        return totalExpense;
    }
    //==================== Оповещения ====================

    private static void checkBudget(String category, double amount) {
        Wallet wallet = currentUser.getWallet();
        Double budget = wallet.getBudgetByCategory().get(category);
        if (budget != null) {
            double spent = wallet.getExpenseByCategory().getOrDefault(category, 0.0);
            if (spent > budget) {
                System.out.println("Внимание! Превышен лимит бюджета для категории '" + category + "'.");
            }
        }
    }
    //==================== Сохранение и загрузка данных ====================
    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            System.out.println("Данные сохранены в файл.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    private static void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (HashMap<String, User>) ois.readObject();
                System.out.println("Данные загружены из файла.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            }
        } else {
            System.out.println("Файл с данными не найден. Будет создан новый файл при сохранении.");
        }
    }

    //==================== Дополнительная функция (Перевод средств) ====================
    private static void transferFunds() {
        System.out.print("Введите логин получателя: ");
        String recipientUsername = scanner.nextLine().trim();
        User recipient = users.get(recipientUsername);

        if (recipient == null) {
            System.out.println("Пользователь с таким логином не найден.");
            return;
        }
        if(recipientUsername.equals(currentUser.getUsername())) {
            System.out.println("Нельзя перевести деньги самому себе.");
            return;
        }

        System.out.print("Введите сумму перевода: ");
        String amountStr = scanner.nextLine().trim();

        if (!amountStr.matches("\\d+(\\.\\d+)?")) {
            System.out.println("Некорректный формат суммы перевода.");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        if (amount <= 0) {
            System.out.println("Сумма перевода должна быть больше нуля.");
            return;
        }
        if (currentUser.getWallet().getTotalBalance() < amount) {
            System.out.println("Недостаточно средств для перевода.");
            return;
        }


        currentUser.getWallet().addExpense("Перевод", amount);
        recipient.getWallet().addIncome("Перевод", amount);

        System.out.println("Перевод успешно выполнен.");
    }
}
