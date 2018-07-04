/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.util.Callback;

/**
 *
 * @author alekseev
 */
public class MbItemEditFormController implements  Initializable {

   @FXML 
    private TableView tableView;
    private TableColumn tableViewColumnKey;
    @FXML
    private TableColumn<?, ?> tableColumnKey;
    @FXML
    private TableColumn<?, ?> tableColumnVolue;
   @FXML 
   private void tableViewOnAdd(ActionEvent event){
      mbItem.getMap().put(0, "Новый");
      ObservableList<Map.Entry<Integer, String>> items = FXCollections.observableArrayList(mbItem.getMap().entrySet());
      tableView.setItems(items);   
      tableView.edit(0, tableViewColumnKey);
       
   }
    private MbItem mbItem;
   MbItemEditFormController(MbItem mbItem) {
    this.mbItem  = mbItem;
    
   }


   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Map.Entry<Integer, String>> items = FXCollections.observableArrayList(mbItem.getMap().entrySet());
        tableView.setItems(items);
        tableViewColumnKey.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, String>, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Integer, String>, Integer> param) {
               return new SimpleObjectProperty<>(param.getValue().getKey());
            }
        });
       tableViewColumnKey.setCellFactory(TextFieldTableCell.forTableColumn());
    }
    
}
