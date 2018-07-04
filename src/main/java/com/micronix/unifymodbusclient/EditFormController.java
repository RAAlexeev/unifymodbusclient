/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

/**
 * FXML Controller class
 *
 * @author alekseev
 */
public class EditFormController implements Initializable {

    @FXML
    private TableView<Map.Entry<Integer, String>> tableView;
    @FXML
    private TableColumn<Map.Entry<Integer, String>, Integer> tableColumnKey;
    @FXML
    private TableColumn<Map.Entry<Integer, String>, String> tableColumnVolue;
   
    private MbItem mbItem;

    void setMbItem(MbItem mbItem){
        this.mbItem =mbItem;
        ObservableList<Map.Entry<Integer, String>> items = FXCollections.observableArrayList(mbItem.getMap().entrySet());
        tableView.setItems(items);
     }
    /**
     * Initializes the controller class.
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {
  

        tableColumnKey.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, String>, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Integer, String>, Integer> param) {
               return new SimpleObjectProperty<>(param.getValue().getKey());
            }
        });
       tableColumnKey.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
       tableColumnKey.setOnEditCommit(new EventHandler<CellEditEvent<Map.Entry<Integer, String>, Integer>>(){
            @Override
            public void handle(CellEditEvent<Map.Entry<Integer, String>, Integer> event) {
                Map.Entry<Integer, String> entry = event.getRowValue();
               mbItem.getMap().remove(entry.getKey());
               mbItem.getMap().merge(event.getNewValue(), entry.getValue(), (value, newValue) -> value.toString()+" \n"+newValue.toString());
               ObservableList<Map.Entry<Integer, String>> items = FXCollections.observableArrayList(mbItem.getMap().entrySet());
               tableView.setItems(items);   
               // if( mbItem.getMap().remove(entry.getKey(), entry.getValue()))incompatible types: incompatible parameter types in lambda expression
                //   mbItem.getMap().put(entry.getKey(),entry);
                       }
       } );
       tableColumnVolue.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, String>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, String>, String> param) {
               return new SimpleObjectProperty<>(param.getValue().getValue());
            }
        });
       tableColumnVolue.setCellFactory(TextFieldTableCell.forTableColumn());
       tableColumnVolue.setOnEditCommit(new EventHandler<CellEditEvent<Map.Entry<Integer, String>, String>>(){
            @Override
            public void handle(CellEditEvent<Map.Entry<Integer, String>, String> event) {
                Map.Entry<Integer, String> entry = event.getRowValue();
                mbItem.getMap().put(entry.getKey(),event.getNewValue());
                entry.setValue(event.getNewValue());
               // if( mbItem.getMap().remove(entry.getKey(), entry.getValue()))incompatible types: incompatible parameter types in lambda expression
                //   mbItem.getMap().put(entry.getKey(),entry);
                       }
       } );
  // TODO
    }    

    @FXML
    private void tableViewOnAdd(ActionEvent event) {
      
      mbItem.getMap().put(new Integer(0), "0");
      ObservableList<Map.Entry<Integer, String>> items = FXCollections.observableArrayList(mbItem.getMap().entrySet());
      tableView.setItems(items);
      
      //tableView.getFocusModel().focus(0);
      //tableView.getSelectionModel().select(tableView.getItems().sorted().getSourceIndex(0), tableColumnKey)
      tableView.edit(tableView.getItems().sorted().getSourceIndex(0), tableColumnKey);
   
    }
    
}
