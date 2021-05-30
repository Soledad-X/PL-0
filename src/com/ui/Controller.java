package com.ui;

import com.entity.Token;
import com.utils.FileUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class Controller {
    @FXML
    private Button readFile;
    @FXML
    private TextArea source;
    @FXML
    private Button lexicalAnalysis;
    @FXML
    private TextField filePath;
    @FXML
    private TableView<Token> lexical;
    @FXML
    private TableColumn<Token, String> row,type,value;

    /**
     *  识别事件来源是否为lexicalAnalysis按钮，若是且source(TextArea对象)非空，进行语法分析操作，否则无操作。
     * @param event ActionEvent事件
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
     */
    @FXML
    public void analysis(ActionEvent event) throws IOException {
        if (event.getSource() == lexicalAnalysis) {
            if(!source.getText().isEmpty()) {
//                PL0 pl0 = new PL0(source.getText().strip()+'\n');
//                row.setCellValueFactory(new PropertyValueFactory<>("row"));
//                type.setCellValueFactory(new PropertyValueFactory<>("type"));
//                value.setCellValueFactory(new PropertyValueFactory<>("value"));
//                ArrayList<Token> tokens = pl0.lexicalAnalysis();
//                lexical.setItems(FXCollections.observableArrayList(tokens));
            } else {
                lexical.getItems().clear();
            }
        }
    }

    /**
     * 识别事件来源是否为readFile按钮，若是且filePath(TextFiled对象)非空，读取文件并显示，否则无操作。
     * @param event ActionEvent事件
     */
    @FXML
    public void readFile(ActionEvent event) {
        if(event.getSource() == readFile && !filePath.getText().isEmpty()){
            source.setText(FileUtil.readFileContent(filePath.getText()));
        }
    }
}
