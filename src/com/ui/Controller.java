package com.ui;

import com.entity.Token;
import com.pl0.GrammarParser;
import com.utils.FileUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class Controller {
    @FXML
    private Button readFile;
    @FXML
    private TextArea source;
    @FXML
    private Button lexicalParserBtn;
    @FXML
    private Button grammarParserBtn;
    @FXML
    private TextField filePath;
    @FXML
    private TableView<Token> lexical;
    @FXML
    private TableColumn<Token, String> Row,Sym,Id,Num;

    @FXML
    private TextArea grammar;

    @FXML
    public void grammarParser(ActionEvent event){
        if (event.getSource() == grammarParserBtn) {
            if(!source.getText().isEmpty()) {
                GrammarParser PL0 = new GrammarParser(source.getText());
                PL0.pl0();
                if(PL0.getErrors().size() == 0) grammar.setText("编译成功");
                else {
                    grammar.clear();
                    ArrayList<String> errors = PL0.getErrors();
                    for(String error : errors){
                        grammar.setText(grammar.getText() + error + "\n");
                    }
                }
            } else {
                grammar.clear();
            }
        }
    }
    /**
     *  识别事件来源是否为lexicalAnalysis按钮，若是且source(TextArea对象)非空，进行语法分析操作，否则无操作。
     *
     * @param event ActionEvent事件
     */
    @FXML
    public void lexicalParser(ActionEvent event) {
        if (event.getSource() == lexicalParserBtn) {
            if(!source.getText().isEmpty()) {
                GrammarParser PL0 = new GrammarParser(source.getText());
                PL0.pl0();
                Row.setCellValueFactory(new PropertyValueFactory<>("ROW"));
                Sym.setCellValueFactory(new PropertyValueFactory<>("SYM"));
                Id.setCellValueFactory(new PropertyValueFactory<>("ID"));
                Num.setCellValueFactory(new PropertyValueFactory<>("NUM"));
                ArrayList<Token> tokens = PL0.getTokens();
                lexical.setItems(FXCollections.observableArrayList(tokens));
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
