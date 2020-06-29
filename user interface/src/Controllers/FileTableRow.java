package Controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class FileTableRow {
    private SimpleStringProperty name;
    private SimpleIntegerProperty id;

    public FileTableRow(int file_id ,String file_name){
        name = new SimpleStringProperty(file_name);
        id = new SimpleIntegerProperty(file_id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }
}
