package Controllers;

import Analyzer.Analyze;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ForeachWindowController {

    @FXML
    private TextField resultTextField;

    @FXML
    private Button analizeButton;

    @FXML
    private TextArea inputTextArea;

    public void initialize(){
        inputTextArea.setText("char[] array = new char[25];\nforeach (char c in array)\n{\n}");
    }

    public void analizeConstr(Event event){
        if(event.getSource().equals(analizeButton)) {
            resultTextField.clear();
            Object[] result = Analyze.analyzeForeach(inputTextArea.getText());
            if((int)result[0] == -1){
                resultTextField.setText((String)result[2] + ", цикл выполнится " + result[1] + " раз");
            }else{
                resultTextField.setText((String)result[2]);
                inputTextArea.positionCaret((int)result[0]-1);
                inputTextArea.selectPositionCaret((int)result[0]);
            }
        }
    }
}
