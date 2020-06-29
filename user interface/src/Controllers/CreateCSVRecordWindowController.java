package Controllers;

import Analyzer.File;
import Exceptions.NoSuchRecordException;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDate;

public class CreateCSVRecordWindowController {
    @FXML
    private TextField resourceAddressField;

    @FXML
    private RadioButton freeAccessMode;

    @FXML
    private DatePicker datePicker;

    @FXML
    private RadioButton closedAccessMode;

    @FXML
    private ToggleGroup chooseAccessMode;

    @FXML
    public void initialize(){
        datePicker.setValue(LocalDate.now());
    }

    public String precessResult() throws NoSuchRecordException {
        String resourceAddress = resourceAddressField.getText().trim();
        if (!resourceAddress.isEmpty() && datePicker.getValue() != null) {
            String accessMode = chooseAccessMode.getSelectedToggle().equals(freeAccessMode) ? "Свободный" : "Закрытый";
            LocalDate creationDate = datePicker.getValue();
            File.addRecordToFile(
                    resourceAddress,
                    chooseAccessMode.getSelectedToggle().equals(freeAccessMode),
                    creationDate);
            return String.format(
                    "%s\t\t%s\t\t%s",
                    resourceAddress,
                    accessMode,
                    creationDate);
        } else throw new NoSuchRecordException();
    }
}
