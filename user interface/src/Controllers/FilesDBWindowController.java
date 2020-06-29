package Controllers;

import Analyzer.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilesDBWindowController {

    @FXML
    private Button openFileButton;
    @FXML
    private Button deleteFileButton;
    @FXML
    private Button addFileButton;
    @FXML
    private TableView<FileTableRow> fileTable;
    @FXML
    private ProgressBar progress;
    private HashMap<Integer, String> map;
    private ArrayList<FileTableRow> rowList;
    private static String selectedFileName;
    private static int selectedFileID;

    @FXML
    public void initialize() {
        if (MainWindowController.getFileName().isEmpty()) {
            addFileButton.setDisable(true);
            openFileButton.setDisable(true);
            deleteFileButton.setDisable(true);
        }
        fileTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileTableRow>() {
            @Override
            public void changed(ObservableValue<? extends FileTableRow> observable, FileTableRow oldValue, FileTableRow newValue) {
                if(newValue != null){
                    openFileButton.setDisable(false);
                    deleteFileButton.setDisable(false);
                    selectedFileName = newValue.getName();
                    selectedFileID = newValue.getId();
                }
            }
        });
        updateTable();
    }

    private void updateTable(){
        try {
            map = File.getFilesFromDB();
            rowList = new ArrayList<>();
            for (Integer id : map.keySet()) {
                FileTableRow row = new FileTableRow(id, map.get(id));
                rowList.add(row);
            }
            ObservableList<FileTableRow> rows = FXCollections.observableArrayList(rowList);
            fileTable.setItems(rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonClicked(ActionEvent event) {
        if (event.getSource().equals(openFileButton)) {
            Dialog<ButtonType> dialog = new Dialog<>();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("RecordsDBWindow.fxml"));
            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.setX(1300);
            //dialog.setY(500);
            dialog.showAndWait();
        } else if (event.getSource().equals(deleteFileButton)) {
            try {
                File.deleteFromDB(selectedFileID, selectedFileName);
                updateTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (event.getSource().equals(addFileButton)) {
            try {
                File.putToDB(MainWindowController.getFileType(), MainWindowController.getFileName());
                updateTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getSelectedFileName(){
        return selectedFileName;
    }

    public static int getSelectedFileID(){
        return selectedFileID;
    }
}

//class GetAllFilesTask extends Task{
//
//    @Override
//    public ObservableList<> call() {
//        try {
//            return FXCollections.observableArrayList(
//                    File.getFilesFromDB()
//            );
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//    }
//}