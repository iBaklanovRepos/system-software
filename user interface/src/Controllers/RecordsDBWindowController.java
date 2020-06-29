package Controllers;

import Analyzer.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class RecordsDBWindowController {

    @FXML
    private TableColumn col_2;
    @FXML
    private TableColumn col_3;
    @FXML
    private TableColumn col_4;
    @FXML
    private TableView<RecordTableRow> recordTable;
    private ArrayList<RecordTableRow> rowList = new ArrayList<>();


    @FXML
    public void initialize(){
        String file_name = FilesDBWindowController.getSelectedFileName();
        int file_id = FilesDBWindowController.getSelectedFileID();
        String substring = file_name.substring(file_name.length() - 4);
        if(substring.equals(".xml")){
            col_2.setText("Имя файла");
            col_3.setText("Версия файла");
            col_4.setText("Дата редактирования");
        }else if(substring.equals(".csv")){
            col_2.setText("Адрес ресурса");
            col_3.setText("Режим доступа");
            col_4.setText("Дата доступа");
        }
        try {
            ArrayList<Object> arrayList = File.getRecordsFromDB(file_id, file_name);
            for(int i = 0; i < arrayList.size(); i += 4){
                RecordTableRow row  = new RecordTableRow((int) arrayList.get(i), (String)arrayList.get(i+1), (String)arrayList.get(i+2), (String)arrayList.get(i+3));
                rowList.add(row);
            }
            ObservableList<RecordTableRow> rows = FXCollections.observableArrayList(rowList);
            recordTable.setItems(rows);
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

}
