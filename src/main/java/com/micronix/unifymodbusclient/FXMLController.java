package com.micronix.unifymodbusclient;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    private TreeItem treeItemRoot;
    @FXML 
    private TreeTableView<MbItem> treeTableView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        treeItemRoot = new TreeItem();
        
        // TODO
        
    }    
    
}
