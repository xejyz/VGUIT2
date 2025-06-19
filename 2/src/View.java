import java.util.Scanner;

public class View {
    private final Scanner scanner = new Scanner(System.in);

    public String getInputExpression() {
        System.out.print("Введите математическое выражение: ");
        return scanner.nextLine();
    }

    public void displayResult(double result) {
        System.out.println("Результат: " + result);
    }

    public void displayError(String message) {
        System.out.println("Ошибка: " + message);
    }
}
