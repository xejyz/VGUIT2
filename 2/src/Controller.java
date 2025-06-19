public class Controller {
    private final Model model;
    private final View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void run() {
        String expression = view.getInputExpression();
        try {
            double result = model.evaluate(expression);
            view.displayResult(result);
        } catch (Exception e) {
            view.displayError(e.getMessage());
        }
    }
}
