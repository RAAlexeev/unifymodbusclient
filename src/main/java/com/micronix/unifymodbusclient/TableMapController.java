package com.micronix.unifymodbusclient;


import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alekseev
 * @param <K>
 */
public class TableMapController<K,V> extends TableView<Map.Entry<K,V>> implements Initializable {
    @FXML
    private TableColumn<K,V> tableColumnKey;
    @FXML
    private TableColumn<K,V> tableColumnVolue;

    public void initialize(URL url, ResourceBundle rb){
    
    }
public TableMapController(ObservableMap<K,V> map, String col1Name, String col2Name) {
    System.out.println("Costruttore table");
    TableColumn<Map.Entry<K, V>, K> column1 = new TableColumn<>(col1Name);
    column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<K, V>, K>, ObservableValue<K>>() {

        @Override
        public ObservableValue<K> call(TableColumn.CellDataFeatures<Map.Entry<K, V>, K> p) {
            // this callback returns property for just one cell, you can't use a loop here
            // for first column we use key
            return new SimpleObjectProperty<K>(p.getValue().getKey());
        }
    });

    TableColumn<Map.Entry<K, V>, V> column2 = new TableColumn<>(col2Name);
    column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<K, V>, V>, ObservableValue<V>>() {

        @Override
        public ObservableValue<V> call(TableColumn.CellDataFeatures<Map.Entry<K, V>, V> p) {
            // for second column we use value
            return new SimpleObjectProperty<V>(p.getValue().getValue());
        }
    });

    ObservableList<Map.Entry<K, V>> items = FXCollections.observableArrayList(map.entrySet());
    this.setItems(items);

    this.getColumns().setAll(column1, column2);

}
}
