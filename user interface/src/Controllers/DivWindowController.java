package Controllers;

import ASM.DivJNI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class DivWindowController {

    @FXML
    private TextField firstNumber;
    @FXML
    private TextField secondNumber;
    @FXML
    private TextField resultField;

    public void countButtonClicked() {
        try{
            resultField.setText(DivJNI.asmDivision(Integer.parseInt(firstNumber.getText()), Integer.parseInt(secondNumber.getText())));
        }catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText("Неверный формат данных");
            alert.showAndWait();
        }
    }
}
