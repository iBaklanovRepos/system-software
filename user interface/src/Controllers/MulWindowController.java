package Controllers;
import ASM.MulJNI;
import Exceptions.FileAlreadyExistsException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MulWindowController {

    @FXML
    private TextField firstNumber;
    @FXML
    private TextField secondNumber;
    @FXML
    private TextField resultField;
    @FXML
    private Label overflowBitLabel;


    public void countButtonClicked(){
        try {
            String[] s;
            s = MulJNI.asmMultiply(Integer.parseInt(firstNumber.getText()), Integer.parseInt(secondNumber.getText())).split("\t");
            resultField.setText(s[0]);
            overflowBitLabel.setText("Бит переполнения - " + s[1]);
        }catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText("Неверный формат данных");
            alert.showAndWait();
        }
    }
}
