package Controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RecordTableRow {
    private SimpleIntegerProperty id;
    private SimpleStringProperty str1;
    private SimpleStringProperty str2;
    private SimpleStringProperty str3;

    public RecordTableRow(int id, String str1, String str2, String str3){
        this.id = new SimpleIntegerProperty(id);
        this.str1 = new SimpleStringProperty(str1);
        this.str2 = new SimpleStringProperty(str2);
        this.str3 = new SimpleStringProperty(str3);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getStr1() {
        return str1.get();
    }

    public SimpleStringProperty str1Property() {
        return str1;
    }

    public String getStr2() {
        return str2.get();
    }

    public SimpleStringProperty str2Property() {
        return str2;
    }

    public String getStr3() {
        return str3.get();
    }

    public SimpleStringProperty str3Property() {
        return str3;
    }
}
