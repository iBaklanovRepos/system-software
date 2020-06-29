package Controllers;

import Analyzer.Analyze;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WhileWindowController {
    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextField resultTextField;
    @FXML
    private Button analizeButton;

    public void initialize() {
        inputTextArea.setText("int n = 0;\n" +
                "while (n < 5)\n" +
                "{\n" +
                "}");
    }

    public void analizeConstr() {
        Object[] result = Analyze.analyzeWhile(inputTextArea.getText());
        if ((int) result[0] == -1) {
            resultTextField.setText(result[2] + ", " + result[1]);
        } else {
            resultTextField.setText((String) result[2]);
            inputTextArea.positionCaret((int)result[0]-1);
            inputTextArea.selectPositionCaret((int)result[0]);
        }
    }
}
