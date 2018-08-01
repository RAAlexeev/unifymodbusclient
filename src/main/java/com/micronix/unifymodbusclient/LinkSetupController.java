/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import jssc.SerialPortList;

/**
 * FXML Controller class
 *
 * @author alekseev
 */
public class LinkSetupController implements Initializable {

    @FXML
    private TextField textFieldPeriod;
    @FXML
    private ChoiceBox<String> choiceBoxPortParity;
    @FXML
    private ChoiceBox<String> choiceBoxPortName;
    @FXML
    private ChoiceBox<Integer> choiceBoxPortBits;
    @FXML
    private ChoiceBox<Integer> choiceBoxPortStoBit;
    @FXML
    private ChoiceBox<SerialPort.BaudRate> choiceBoxBaudrate;
    @FXML
    private ChoiceBox<String> choiceBoxType;
    @FXML
    private ChoiceBox<Integer> choiceBoxAddrCorect;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       choiceBoxPortName.getItems().setAll( Arrays.asList( SerialPortList.getPortNames() ));   
       this.choiceBoxBaudrate.getItems().setAll( Arrays.asList(SerialPort.BaudRate.values()));
       this.choiceBoxPortBits.getItems().setAll(8,7);
       for ( SerialPort.Parity parity : SerialPort.Parity.values()){
            this.choiceBoxPortParity.getItems().add(parity.name());
       }
       this.choiceBoxPortStoBit.getItems().addAll(1,2);
       this.choiceBoxType.getItems().addAll("RTU","ASCII");
       this.textFieldPeriod.textProperty().addListener((observable, oldValue, newValue) -> {
             if (!newValue.matches("\\d*")) {
                  this.textFieldPeriod.setText(oldValue);
                 }
        });
       this.choiceBoxAddrCorect.getItems().addAll(0, -1);
        // TODO
    }    
    public String getType(){
        return this.choiceBoxType.getValue();
    }
    
    public void setType(String type){
          this.choiceBoxType.setValue(type);
    }
    
    private SerialParameters serialParameters;
    
    public void setData(SerialParameters p, String type, int period){
       serialParameters = p;
       this.choiceBoxPortName.setValue(p.getDevice());
       this.choiceBoxBaudrate.setValue(SerialPort.BaudRate.getBaudRate(p.getBaudRate()));  
       this.choiceBoxPortBits.setValue(p.getDataBits());
       this.choiceBoxPortParity.getSelectionModel().select(p.getParity().getValue());
       this.choiceBoxPortStoBit.setValue(p.getStopBits());
       this.choiceBoxType.setValue(type);
       this.textFieldPeriod.setText(Integer.toString(period));
       this.choiceBoxAddrCorect.setValue(MbItem.addrCorrect);
    }
    
    public SerialParameters getSerialParameters(){
       serialParameters.setBaudRate(this.choiceBoxBaudrate.getValue());
       serialParameters.setDataBits(this.choiceBoxPortBits.getValue());
       serialParameters.setDevice(this.choiceBoxPortName.getValue());
       serialParameters.setParity(SerialPort.Parity.getParity( this.choiceBoxPortParity.getSelectionModel().getSelectedIndex()));
       serialParameters.setStopBits(this.choiceBoxPortStoBit.getValue());
       return serialParameters;
        
    } 
    public Integer getPeriod(){
        
        return Integer.parseInt(this.textFieldPeriod.getText());
    }
    public Integer getAddrCorect(){
        return this.choiceBoxAddrCorect.getValue();
    }
}
