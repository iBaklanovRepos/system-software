package Controllers;

import Analyzer.CSVRecord;
import Analyzer.File;
import Exceptions.NoSuchRecordException;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDate;

public class EditCSVRecordController {
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

    private int index;

    public void init(MainWindowController controller){
        index = controller.getSelectedItemIndex();
        try {
            CSVRecord record = (CSVRecord) File.getRecordFromFile(".csv", index);
            resourceAddressField.setText(record.getResourceAddress());
            if (record.isAccessMode()) {
                chooseAccessMode.selectToggle(freeAccessMode);
            } else {
                chooseAccessMode.selectToggle(closedAccessMode);
            }
            datePicker.setValue(record.getAccessDate());
        }catch (NoSuchRecordException e) {
        }
    }

    public String precessResult(int position) throws NoSuchRecordException {
        if (!resourceAddressField.getText().isEmpty() && datePicker.getValue() != null) {
            String resourceAddress = resourceAddressField.getText();
            String accessMode = chooseAccessMode.getSelectedToggle().equals(freeAccessMode) ? "Свободный" : "Закрытый";
            LocalDate creationDate = datePicker.getValue();
            File.editFile(
                    position,
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
