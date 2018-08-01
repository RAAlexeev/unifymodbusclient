/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

/**
 * FXML Controller class
 *
 * @author alekseev
 */
public class EditFormController implements Initializable {
    private boolean isEdited = false;
    @FXML
    private TextField textFieldDot;
    @FXML
    private Button buttonWrite;
    @FXML
    private TextField textFieldMin;
    @FXML
    private TextField textFieldMax;

    @FXML
    private void buttonWriteOnAction(ActionEvent event) {
           try {
                if(MbItem.code.get(mbItem.getServer()) != null){
                    modbus.getModbusMaster().writeSingleRegister( mbItem.getServer(), MbItem.code.get(mbItem.getServer()).getAddr(), MbItem.code.get(mbItem.getServer()).getRawDefaultValue() );                   
                } 
                modbus.getModbusMaster().writeSingleRegister( mbItem.getServer(), mbItem.getAddr(), mbItem.getRawDefaultValue() );          
            } catch (ModbusProtocolException | ModbusNumberException | ModbusIOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE,"Не удалось передать!" + ex.getMessage());
            }
    }

    @FXML
    private TableView<Map.Entry<Integer, String>> tableView;
    @FXML
    private TableColumn<Map.Entry<Integer, String>, Integer> tableColumnKey;
    @FXML
    private TableColumn<Map.Entry<Integer, String>, String> tableColumnVolue;
   
    private MbItem mbItem;
    private Modbus modbus;
    @FXML
    private TextField textFieldDefaultValue;
    @FXML
    private ChoiceBox<String> ChoiceBoxFunc;

    private MbItem saveMbItem;
    
    void setData(MbItem mbItem, Modbus modbus,int period){
        this.modbus = modbus;
        this.mbItem = mbItem;
        saveMbItem = new MbItem(mbItem);
        ObservableList<Map.Entry<Integer, String>> items = FXCollections.observableArrayList(mbItem.getMap().entrySet());
   /*     items.addListener( new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
               
            }
        } );*/
        tableView.setItems(items);
        if(mbItem.getRawDefaultValue() != null)
        switch(mbItem.getType()){
            case bits:
                   textFieldDefaultValue.setText( Integer.toBinaryString( mbItem.getRawDefaultValue()) );
               break;
            case float32:
                 textFieldDefaultValue.setText(Float.toString(Float.intBitsToFloat(mbItem.getRawDefaultValue())) );
               break;
            default:
                textFieldDefaultValue.setText(mbItem.getRawDefaultValue().toString());
        }

        if( mbItem.getFunc() != null )
            this.ChoiceBoxFunc.getSelectionModel().select( mbItem.getFunc().getDescription() );
        if (mbItem.getRawDefaultValue() == null){ 
            this.buttonWrite.setDisable(true);
        }else{
            this.textFieldMax.setVisible(false);
            this.textFieldMin.setVisible(false);
        }
        if( mbItem.getMax()!= null )
            this.textFieldMax.setText( mbItem.getMax().toString());
        if( mbItem.getMin()!= null )
            this.textFieldMin.setText( mbItem.getMin().toString());
        
        this.textFieldDot.setText( mbItem.getPoint()!= null?mbItem.getPoint().toString():"" );
     }
        
    /**
     * Initializes the cont roller class.
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
        EditFormController self = this;
       tableColumnKey.setOnEditCommit(new EventHandler<CellEditEvent<Map.Entry<Integer, String>, Integer>>(){
            @Override
            public void handle(CellEditEvent<Map.Entry<Integer, String>, Integer> event) {
                Map.Entry<Integer, String> entry = event.getRowValue();
             
               mbItem.getMap().remove(entry.getKey());
               mbItem.getMap().merge(event.getNewValue(), entry.getValue(), (value, newValue) -> value.toString()+" \n"+newValue.toString());
               ObservableList<Map.Entry<Integer, String>> items = FXCollections.observableArrayList(mbItem.getMap().entrySet());
               tableView.setItems(items);
                                  
              tableView.edit(event.getTablePosition().getRow(), tableView.getColumns().get(1));
              isEdited = true;  
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
                isEdited = true;  
               // if( mbItem.getMap().remove(entry.getKey(), entry.getValue()))incompatible types: incompatible parameter types in lambda expression
                //   mbItem.getMap().put(entry.getKey(),entry);
                       }
       } );
       this.textFieldDefaultValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()){
              mbItem.setDefaultValue(null);  
              return;
            }
            try{ 
                switch(mbItem.getType()){
                    case bits:
                        mbItem.setDefaultValue( Integer.parseInt(newValue, 2));
                        break;
                    default:
                         mbItem.setDefaultValue( Integer.parseInt(newValue));
                }
                 this.buttonWrite.setDisable(false);
                 this.textFieldMax.setVisible(false);
                 this.textFieldMin.setVisible(false);
                 isEdited = true; 
            }catch(NumberFormatException ex){
               this.textFieldDefaultValue.setText(oldValue);
            }

        });
        String[] fd= new String[MbItem.Func.values().length];
        int i = 0;
        for (MbItem.Func f :  MbItem.Func.values())
            fd[i++] = f.getDescription();
        this.ChoiceBoxFunc.getItems().setAll(Arrays.asList( fd ));
        this.ChoiceBoxFunc.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue)->{
            this.mbItem.setFunc(MbItem.Func.values()[newValue.intValue()]);
        });
        this.textFieldDot.textProperty().addListener((observable, oldValue, newValue) -> {

            try{
                mbItem.setPoint(Integer.parseInt(newValue));
            }catch(NumberFormatException ex){
               this.textFieldDot.setText(oldValue);
             }
                 
        });
         this.textFieldDot.setOnKeyPressed(evnt -> {
             if(evnt.getCode().equals(KeyCode.ESCAPE)){
                 mbItem.setPoint(saveMbItem.getPoint());
                 textFieldDot.setText(mbItem.getPoint().toString());
             }
                     
         });

                this.textFieldMin.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()){
              mbItem.setMin(null);  
              return;
            }
         try{                     
                mbItem.setMin(Float.parseFloat(newValue));
            }catch(NumberFormatException ex){
               this.textFieldMax.setText(oldValue);
            }
        });
        this.textFieldMax.textProperty().addListener((observable, oldValue, newValue) -> {
            if( newValue.isEmpty() ){
              mbItem.setMax(null);  
              return;
            }
            try{            
                 mbItem.setMax(Float.parseFloat(newValue));
                 isEdited = true; 
            }catch(NumberFormatException ex){
               this.textFieldMax.setText(oldValue); 
            }                        
        });
       // TODO
    }    

    @FXML
    private void tableViewOnAdd(ActionEvent event) {
      
      mbItem.getMap().put(null, "###");
      ObservableList<Map.Entry<Integer, String>> items = FXCollections.observableArrayList(mbItem.getMap().entrySet());
      tableView.setItems(items);
      isEdited = true;  
      //tableView.getFocusModel().focus(0);
      //tableView.getSelectionModel().select(tableView.getItems().sorted().getSourceIndex(0), tableColumnKey)
      tableView.edit(tableView.getItems().size()-1, tableColumnKey);
   
    }
    public boolean getIsEdited(){
        return this.isEdited;
    }
    
}
