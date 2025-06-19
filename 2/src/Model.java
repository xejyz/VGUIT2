import java.util.*;
import java.util.regex.*;

public class Model {

    public double evaluate(String expression) throws Exception {
        if (!isValid(expression)) {
            throw new Exception("Недопустимое выражение или количество слагаемых превышает 15.");
        }

        expression = preprocess(expression);
        List<String> rpn = infixToRPN(expression);
        return evaluateRPN(rpn);
    }

    private boolean isValid(String expr) {
        int balance = 0;
        int operandCount = 0;

        String exprNoSpaces = expr.replaceAll("\\s+", "");
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(exprNoSpaces);
        while (matcher.find()) operandCount++;

        for (char c : exprNoSpaces.toCharArray()) {
            if (c == '(') balance++;
            else if (c == ')') balance--;
            if (balance < 0) return false;
        }

        return balance == 0 && operandCount <= 15;
    }

    private String preprocess(String expr) {
        expr = expr.replaceAll("\\s+", "");
        expr = expr.replaceAll("\\*\\*", "^");
        expr = expr.replaceAll("log\\(", "l(");
        expr = expr.replaceAll("exp\\(", "e(");
        return expr;
    }

    private int precedence(String op) {
        return switch (op) {
            case "!" -> 5;
            case "^" -> 4;
            case "*", "/", "#" -> 3;
            case "+", "-" -> 2;
            case "l", "e" -> 6; // log, exp
            default -> 0;
        };
    }

    private boolean isLeftAssociative(String op) {
        return !op.equals("^") && !op.equals("!");
    }

    private List<String> infixToRPN(String expr) throws Exception {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        Matcher matcher = Pattern.compile(
                        "log\\(|exp\\(|[()]|\\d+(\\.\\d+)?|[+\\-*/^!]|\\*\\*|//")
                .matcher(expr);

        while (matcher.find()) {
            String token = matcher.group();
            if (token.matches("\\d+(\\.\\d+)?")) {
                output.add(token);
            } else if (token.equals("l") || token.equals("e")) {
                stack.push(token);
            } else if (token.equals("!")) {
                output.add(token);
            } else if (token.matches("[+\\-*/^#]")) {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(token)
                        && isLeftAssociative(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new Exception("Скобки несбалансированы");
                stack.pop();
                if (!stack.isEmpty() && (stack.peek().equals("l") || stack.peek().equals("e"))) {
                    output.add(stack.pop());
                }
            }
        }

        while (!stack.isEmpty()) {
            String token = stack.pop();
            if (token.equals("(")) throw new Exception("Скобки несбалансированы");
            output.add(token);
        }

        return output;
    }

    private double evaluateRPN(List<String> rpn) throws Exception {
        Stack<Double> stack = new Stack<>();
        for (String token : rpn) {
            if (token.matches("\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else if (token.equals("!")) {
                double a = stack.pop();
                stack.push((double) factorial((int) a));
            } else if (token.equals("l")) {
                double a = stack.pop();
                stack.push(Math.log(a) / Math.log(2));
            } else if (token.equals("e")) {
                double a = stack.pop();
                stack.push(Math.exp(a));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(switch (token) {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" -> a / b;
                    case "^" -> Math.pow(a, b);
                    case "#" -> (double) ((int) a / (int) b);
                    default -> throw new Exception("Неизвестная операция: " + token);
                });
            }
        }
        return stack.pop();
    }

    private int factorial(int n) throws Exception {
        if (n < 0) throw new Exception("Факториал отрицательного числа недопустим");
        int result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
}
