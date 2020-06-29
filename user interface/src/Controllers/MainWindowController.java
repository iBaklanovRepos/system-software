package Controllers;

import Analyzer.CSVFile;
import Analyzer.File;
import Analyzer.XMLFile;
import Analyzer.XMLRecord;
import Exceptions.FileAlreadyExistsException;
import Exceptions.InvalidFileExtensionException;
import Exceptions.NoSuchRecordException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

public class MainWindowController {
    private java.io.File currentFile;
    private static String currentFileName;
    private static String currentFileExtension;
    private String currentDBPath;
    private String dbName;
    private int selectedItemIndex;
    private int currentFile_id;

    @FXML
    public MenuItem chooseDBItem;

    @FXML
    public MenuItem createDBItem;

    @FXML
    public MenuItem openDBItem;

    @FXML
    private MenuItem foreachMenuItem;

    @FXML
    private MenuItem whileMenuItem;

    @FXML
    private MenuItem divMenuItem;

    @FXML
    private MenuItem mulMenuItem;

    @FXML
    private Menu fileMenu;

    @FXML
    private Button saveLogButton;

    @FXML
    private TextArea programLogTextArea;

    @FXML
    private MenuItem renameFileMenuItem;

    @FXML
    private Button editRecordButton;

    @FXML
    private Button addRecordButton;

    @FXML
    private MenuItem createFileMenuWindow;

    @FXML
    private MenuItem openFileMenuItem;

    @FXML
    private MenuItem saveFileMenuItem;

    @FXML
    private MenuItem saveAsFileMenuItem;

    @FXML
    private Label fileNameLabel;

    @FXML
    private MenuItem deleteFileMenuItem;

    @FXML
    private VBox vboxMainWindow;

    @FXML
    private Button deleteRecordButton;

    @FXML
    private ListView listFileView;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    public void initialize() {
        deleteRecordButton.setDisable(true);
        editRecordButton.setDisable(true);
        addRecordButton.setDisable(true);
        saveFileMenuItem.setDisable(true);
        saveAsFileMenuItem.setDisable(true);
        deleteFileMenuItem.setDisable(true);
        renameFileMenuItem.setDisable(true);
        openDBItem.setDisable(true);
        currentFileName = "";


        listFileView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (listFileView.getItems().isEmpty()) {
                    editRecordButton.setDisable(true);
                    deleteRecordButton.setDisable(true);
                } else {
                    editRecordButton.setDisable(false);
                    deleteRecordButton.setDisable(false);
                }
            }
        });
    }

    @FXML
    public void dragFileOver(DragEvent event) {
        if (event.getGestureSource() != vboxMainWindow && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    @FXML
    public void dropDraggedFile(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            currentFile = dragboard.getFiles().get(0);
            currentFileName = currentFile.getName();
            currentFileExtension = currentFileName.substring(currentFileName.length() - 4);
            if (currentFileExtension.equals(".xml")) {
                try {
                    XMLFile file = (XMLFile) File.loadFile(currentFile);
                    fileNameLabel.setText(currentFileName);
                    LogWriter.writeLog(this, "Файл " + currentFileName + " открыт");
                    listFileView.getItems().remove(0, listFileView.getItems().size());
                    for (int i = 0; i < File.getFileSize(currentFileExtension); i++) {
                        XMLRecord record = (XMLRecord) file.getRecord(i);
                        listFileView.getItems().add(String.format(
                                "%s\t\t%s\t\t%s",
                                record.getFileName(),
                                record.getFileVersion(),
                                record.getLastEditDate()));
                    }
                    addRecordButton.setDisable(false);
                    saveFileMenuItem.setDisable(false);
                    saveAsFileMenuItem.setDisable(false);
                    deleteFileMenuItem.setDisable(false);
                    renameFileMenuItem.setDisable(false);
                } catch (IOException | NoSuchRecordException | InvalidFileExtensionException e) {
                    e.printStackTrace();
                }
            } else if (currentFileExtension.equals(".csv")) {
                try {
                    CSVFile file = (CSVFile) File.loadFile(currentFile);
                    fileNameLabel.setText(currentFileName);
                    LogWriter.writeLog(this, "Файл " + currentFileName + " открыт");
                    listFileView.getItems().remove(0, listFileView.getItems().size());
                    for (int i = 0; i < file.getFileSize(); i++) {
                        Analyzer.CSVRecord record = file.getRecord(i);
                        String accessMode = record.isAccessMode() ? "Свободный" : "Закрытый";
                        listFileView.getItems().add(String.format(
                                "%s\t\t%s\t\t%s",
                                record.getResourceAddress(),
                                accessMode,
                                record.getAccessDate())
                        );
                    }
                    addRecordButton.setDisable(false);
                    saveFileMenuItem.setDisable(false);
                    saveAsFileMenuItem.setDisable(false);
                    deleteFileMenuItem.setDisable(false);
                    renameFileMenuItem.setDisable(false);
                } catch (IOException | NoSuchRecordException e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка загрузки файла");
                alert.setHeaderText("Неверный формат файла");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void showCreateFileWindow() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("CreateFileWindow.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.setTitle("Создание файла");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CreateFileWindowController controller = fxmlLoader.getController();
            try {
                currentFileName = controller.processResult();
                currentFileExtension = currentFileName.substring(currentFileName.length() - 4);
                fileNameLabel.setText(currentFileName);
                listFileView.getItems().remove(0, listFileView.getItems().size());
                File.createFile(currentFileExtension);
                LogWriter.writeLog(this, "Файл " + currentFileName + " создан");
                addRecordButton.setDisable(false);
                saveAsFileMenuItem.setDisable(false);
                saveFileMenuItem.setDisable(true);
                deleteFileMenuItem.setDisable(true);
                renameFileMenuItem.setDisable(false);
            } catch (FileAlreadyExistsException e) {
                LogWriter.writeLog(this, "Ошибка при создании файла");
            }
        }
    }

    @FXML
    public void chooseDBFromDesktop() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Database File", "*.db")
        );
        java.io.File dbFile = fileChooser.showOpenDialog(mainBorderPane.getScene().getWindow());
        if (dbFile != null) {
            try {
                currentDBPath = dbFile.getPath();
                dbName = dbFile.getName();
                openDBItem.setDisable(false);
                File.createDB(currentDBPath);
                showDB();
            }catch (SQLException e){
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка открытия БД");
                alert.setHeaderText("Что-то пошло не так :(");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void chooseDirectoryForDB() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку");
        java.io.File file = directoryChooser.showDialog(mainBorderPane.getScene().getWindow());
        if (file != null && file.isDirectory()) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            TextField textField = new TextField();
            dialog.getDialogPane().setContent(textField);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            dialog.setTitle("Создание базы данных");
            dialog.setHeaderText("Введите имя базы данных");
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (!textField.getText().isEmpty()) {
                    try {
                        currentDBPath = file.getPath() + "\\" + textField.getText() + ".db";
                        File.createDB(currentDBPath);
                        dbName = textField.getText() + ".db";
                        openDBItem.setDisable(false);
                        showDB();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка открытия БД");
                        alert.setHeaderText("Что-то пошло не так :(");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка создания БД");
                    alert.setHeaderText("Не все поля заполнены");
                    alert.showAndWait();
                }
            }

        }
    }

    @FXML
    public void chooseFileFromDesktop() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv"),
                new FileChooser.ExtensionFilter("XML File", "*.xml")
        );
        currentFile = fileChooser.showOpenDialog(mainBorderPane.getScene().getWindow());
        if (currentFile != null) {
            try {
                listFileView.getItems().remove(0, listFileView.getItems().size());
                currentFileName = currentFile.getName();
                currentFileExtension = currentFileName.substring(currentFileName.length() - 4);
                fileNameLabel.setText(currentFileName);
                if (currentFileExtension.equals(".xml")) {
                    try {
                        XMLFile xmlFile = (XMLFile) File.loadFile(currentFile);
                        fileNameLabel.setText(currentFileName);
                        LogWriter.writeLog(this, "Файл " + currentFileName + " открыт");
                        listFileView.getItems().remove(0, listFileView.getItems().size());
                        for (int i = 0; i < File.getFileSize(currentFileExtension); i++) {
                            XMLRecord record = (XMLRecord) xmlFile.getRecord(i);
                            listFileView.getItems().add(String.format(
                                    "%s\t\t%s\t\t%s",
                                    record.getFileName(),
                                    record.getFileVersion(),
                                    record.getLastEditDate()));
                        }
                    } catch (IOException | NoSuchRecordException | InvalidFileExtensionException e) {
                        e.printStackTrace();
                    }
                } else if (currentFileExtension.equals(".csv")) {
                    try {
                        CSVFile csvFile = (CSVFile) File.loadFile(currentFile);
                        fileNameLabel.setText(currentFileName);
                        LogWriter.writeLog(this, "Файл " + currentFileName + " открыт");
                        listFileView.getItems().remove(0, listFileView.getItems().size());
                        for (int i = 0; i < csvFile.getFileSize(); i++) {
                            Analyzer.CSVRecord record = csvFile.getRecord(i);
                            String accessMode = record.isAccessMode() ? "Свободный" : "Закрытый";
                            listFileView.getItems().add(String.format(
                                    "%s\t\t%s\t\t%s",
                                    record.getResourceAddress(),
                                    accessMode,
                                    record.getAccessDate())
                            );
                        }
                    } catch (IOException | NoSuchRecordException e) {
                        e.printStackTrace();
                    }
                }
                addRecordButton.setDisable(false);
                saveFileMenuItem.setDisable(false);
                saveAsFileMenuItem.setDisable(false);
                deleteFileMenuItem.setDisable(false);
                renameFileMenuItem.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    public void chooseDirectoryFromDesktop() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку");
        java.io.File file = directoryChooser.showDialog(mainBorderPane.getScene().getWindow());
        if (file != null && file.isDirectory()) {
            try {
                currentFile = new java.io.File(file.getPath() + "\\" + currentFileName);
                File.saveFile(currentFile);
                LogWriter.writeLog(this, "Файл " + currentFileName + " сохранен");
                saveFileMenuItem.setDisable(false);
                deleteFileMenuItem.setDisable(false);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка сохранения файла");
                alert.setHeaderText("Что-то пошло не так :(");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void deleteFileConfirm() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setTitle("Удаление файла");
        dialog.setHeaderText("Подтвердите удаление файла " + currentFileName);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.APPLY) {
            File.deleteFile(currentFileName);
            currentFile.delete();
            LogWriter.writeLog(this, "Файл " + currentFileName + " удален");
            currentFileName = "";
            listFileView.getItems().remove(0, listFileView.getItems().size());
            fileNameLabel.setText("Перетащите свой файл");
            addRecordButton.setDisable(true);
            saveFileMenuItem.setDisable(true);
            saveAsFileMenuItem.setDisable(true);
            deleteFileMenuItem.setDisable(true);
            renameFileMenuItem.setDisable(true);
        }
    }

    @FXML
    public void showCreateRecordWindow() {
        if (currentFileExtension.equals(".xml")) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("dll File", "*.dll"));
            java.io.File file = fileChooser.showOpenDialog(mainBorderPane.getScene().getWindow());
            if (file != null) {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(mainBorderPane.getScene().getWindow());
                TextField textField = new TextField();
                dialog.getDialogPane().setContent(textField);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
                dialog.setTitle("Создание XML записи");
                dialog.setHeaderText("Введите версию файла");
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (!textField.getText().isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        File.addRecordToFile(
                                file.getName(),
                                textField.getText(),
                                LocalDate.parse(sdf.format(file.lastModified())));
                        listFileView.getItems().add(String.format(
                                "%s\t\t%s\t\t%s",
                                file.getName(),
                                textField.getText(),
                                LocalDate.parse(sdf.format(file.lastModified()))));
                        LogWriter.writeLog(this, "Создана запись XML");
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка создания записи");
                        alert.setHeaderText("Не все поля заполнены");
                        alert.showAndWait();
                    }

                }


            }
        } else if (currentFileExtension.equals(".csv")) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CreateCSVRecordWindow.fxml"));
            try {
                dialog.getDialogPane().setContent(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    CreateCSVRecordWindowController controller = loader.getController();
                    String newRecord = controller.precessResult();
                    listFileView.getItems().add(newRecord);
                    LogWriter.writeLog(this, "Создана запись CSV");
                } catch (NoSuchRecordException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка создания записи");
                    alert.setHeaderText("Не все поля заполнены");
                    alert.showAndWait();
                }
            }
        }
    }

    @FXML
    public void showRenameDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        TextField textField = new TextField();
        textField.setText(currentFileName.substring(0, currentFileName.length() - 4));
        dialog.getDialogPane().setContent(textField);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setTitle("Изменение имени файла");
        dialog.setHeaderText("Введите новое имя файла");
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (!textField.getText().isEmpty()) {
                currentFileName = textField.getText() + currentFileExtension;
                LogWriter.writeLog(this, "Файл переименован");
                fileNameLabel.setText(currentFileName);
                saveFileMenuItem.setDisable(true);
                deleteFileMenuItem.setDisable(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка изменения имени файла");
                alert.setHeaderText("Не все поля заполнены");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void showEditRecordWindow() {
        if (currentFileExtension.equals(".csv")) {
            selectedItemIndex = listFileView.getSelectionModel().getSelectedIndex();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("EditCSVRecordWindow.fxml"));
            try {
                dialog.getDialogPane().setContent(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }

            EditCSVRecordController controller = loader.getController();
            controller.init(this);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String newRecord = controller.precessResult(selectedItemIndex);
                    listFileView.getItems().set(selectedItemIndex, newRecord);
                    LogWriter.writeLog(this, "Изменена запись CSV");
                } catch (NoSuchRecordException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка изменения записи");
                    alert.setHeaderText("Не все поля заполнены");
                    alert.showAndWait();
                }
            }
        } else if (currentFileExtension.equals(".xml")) {
            selectedItemIndex = listFileView.getSelectionModel().getSelectedIndex();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("dll File", "*.dll"));
            java.io.File file = fileChooser.showOpenDialog(mainBorderPane.getScene().getWindow());
            if (file != null) {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(mainBorderPane.getScene().getWindow());
                TextField textField = new TextField();
                dialog.getDialogPane().setContent(textField);
                try {
                    XMLRecord record = (XMLRecord) File.getRecordFromFile(currentFileExtension, selectedItemIndex);
                    textField.setText(record.getFileVersion());
                } catch (NoSuchRecordException e) {
                    e.printStackTrace();
                }
                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
                dialog.setTitle("Изменение XML записи");
                dialog.setHeaderText("Введите версию файла");
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (!textField.getText().isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            File.editFile(
                                    selectedItemIndex,
                                    file.getName(),
                                    textField.getText(),
                                    LocalDate.parse(sdf.format(file.lastModified())));
                            listFileView.getItems().set(
                                    selectedItemIndex,
                                    String.format(
                                            "%s\t\t%s\t\t%s",
                                            file.getName(),
                                            textField.getText(),
                                            LocalDate.parse(sdf.format(file.lastModified()))));
                            LogWriter.writeLog(this, "Изменена запись XML");
                        } catch (NoSuchRecordException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка изменения записи");
                        alert.setHeaderText("Не все поля заполнены");
                        alert.showAndWait();
                    }
                }
            }
        }
    }

    @FXML
    public void showMulWindow() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("MulWindow.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.setTitle("Умножение с битом переполнения");
        dialog.showAndWait();
    }

    @FXML
    public void showDivWindow() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("DivWindow.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.setTitle("Разделить одно значение на другое");
        dialog.showAndWait();
    }

    @FXML
    public void showForeachWindow() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("ForeachWindow.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.setTitle("Анализ конструкции Foreach");
        dialog.showAndWait();
    }

    @FXML
    public void showWhileWindow() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("WhileWindow.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.setTitle("Анализ конструкции While");
        dialog.showAndWait();
    }

    @FXML
    public void showDB() {
        Dialog<ButtonType> dialog = new Dialog<>();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("FilesDBWindow.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setTitle(dbName);
        dialog.setX(1200);
        dialog.showAndWait();
    }

    @FXML
    public void menuItemClicked(ActionEvent event) {
        if (event.getSource().equals(createFileMenuWindow)) {
            showCreateFileWindow();
        } else if (event.getSource().equals(openFileMenuItem)) {
            chooseFileFromDesktop();
        } else if (event.getSource().equals(saveAsFileMenuItem)) {
            chooseDirectoryFromDesktop();
        } else if (event.getSource().equals(saveFileMenuItem)) {
            try {
                File.saveFile(currentFile);
                LogWriter.writeLog(this, "Файл " + currentFileName + " сохранен");
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка сохранения файла");
                alert.setHeaderText("Что-то пошло не так :(");
                alert.showAndWait();
            }
        } else if (event.getSource().equals(deleteFileMenuItem)) {
            deleteFileConfirm();
        } else if (event.getSource().equals(renameFileMenuItem)) {
            showRenameDialog();
        } else if (event.getSource().equals(mulMenuItem)) {
            showMulWindow();
        } else if (event.getSource().equals(divMenuItem)) {
            showDivWindow();
        } else if (event.getSource().equals(foreachMenuItem)) {
            showForeachWindow();
        } else if (event.getSource().equals(whileMenuItem)) {
            showWhileWindow();
        } else if (event.getSource().equals(createDBItem)) {
            chooseDirectoryForDB();
        } else if (event.getSource().equals(chooseDBItem)) {
            chooseDBFromDesktop();
        } else if(event.getSource().equals(openDBItem)){
            showDB();
        }
    }

    @FXML
    public void buttonClicked(ActionEvent e) {
        if (e.getSource().equals(deleteRecordButton)) {
            try {
                int index = listFileView.getSelectionModel().getSelectedIndex();
                File.deleteRecordFromFile(currentFileName, index);
                listFileView.getItems().remove(index);
                LogWriter.writeLog(this, "Запись удалена");
                if (listFileView.getItems().isEmpty()) {
                }

            } catch (NoSuchRecordException exception) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка удаления записи");
                alert.setHeaderText("Такой записи не существует");
                alert.showAndWait();
            }
        } else if (e.getSource().equals(editRecordButton)) {
            showEditRecordWindow();
        } else if (e.getSource().equals(addRecordButton)) {
            showCreateRecordWindow();
        } else if (e.getSource().equals(saveLogButton)) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Выберите папку");
            java.io.File file = directoryChooser.showDialog(mainBorderPane.getScene().getWindow());
            new java.io.File(file.getPath() + "\\лог.txt");
        }
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public TextArea getProgramLogTextArea() {
        return programLogTextArea;
    }

    public static String getFileType(){
        return currentFileExtension;
    }

    public static String getFileName(){
        return currentFileName;
    }
}
