package Controllers;

import Analyzer.File;
import Exceptions.FileAlreadyExistsException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class CreateFileWindowController {
    @FXML
    private TextField fileNameField;

    @FXML
    private RadioButton csvRadioButton;

    @FXML
    private RadioButton xmlRadioButton;

    @FXML
    private ToggleGroup chooseFileType;

    public String processResult() throws FileAlreadyExistsException {
        String fileName = fileNameField.getText().trim();
        if (!fileName.isEmpty()) {
            if (chooseFileType.getSelectedToggle().equals(csvRadioButton)) {
                fileName += ".csv";
            } else {
                fileName += ".xml";
            }
            File.createFile(fileName);
            return fileName;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка создания файла");
            alert.setHeaderText("Недопустимое имя файла");
            alert.showAndWait();
            throw new FileAlreadyExistsException();
        }
    }
}
