package com.micronix.unifymodbusclient; 

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortException;
import com.sun.javafx.scene.control.skin.TreeTableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeTableColumn.CellEditEvent;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;

import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;



public class FXMLController implements   Initializable {

 /*   @FXML
    private Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
   */ 
    private Consumer<String> setTitle; 
    private boolean isEdited = false;
    private final FXMLController self = this;
    private TreeItem treeItemRoot;
    private final boolean treeTableViewIsUpdate = false;
    @FXML
    private TreeTableView treeTableView;
    @FXML
    private TreeTableColumn treeTableColumnName;
    @FXML
    private TreeTableColumn<MbItem, Object> treeTableColumnAddr;
    @FXML
    private ContextMenu treeTableViewContextMenu;
    @FXML
    private MenuItem menuItemGroup;
    @FXML
    private TreeTableColumn<MbItem, Object> treeTableColumnType;
    @FXML
    private TreeTableColumn<MbItem, Object> treeTableColumnVolue;
    @FXML
    private TreeTableColumn treeTableColumnBar;
  
    @FXML
    private CheckMenuItem ChekMenuItemReguest;
    @FXML
    private TextArea textAreaLog;
    @FXML
    private TreeTableColumn<MbItem, Object> treeTableColumnDefaultValue;
    @FXML
    private void menuItemGroupOnAction(ActionEvent event) {
        this.treeTableView.getRoot().getChildren().add(new TreeItem( new MbItem( "Устройство", 1 , null )));

    }
    private void menuItemWordOnAction(ActionEvent event) {
       // treeItemRoot.getChildren().add(new TreeItem(new MbItem("Новый элемент","",0,"")));
    }
    private Properties properties = new Properties();
    
    public void setProperties(Properties properties){
        this.properties = properties;
       
           this.serialParameters.setDevice( properties.getProperty( "port" ) );
           this.serialParameters.setBaudRate(SerialPort.BaudRate.valueOf( "BAUD_RATE_" + properties.getProperty( "BaudRate" ) ));
           this.serialParameters.setDataBits(Integer.parseInt(properties.getProperty( "DataBits" )) );
           this.serialParameters.setParity(SerialPort.Parity.valueOf(properties.getProperty( "Parity" )));
           this.serialParameters.setStopBits( Integer.parseInt(properties.getProperty( "StopBits" )) );
        if  ( properties.getProperty( "Period" ) != null )
           this.period = Integer.parseInt( properties.getProperty( "Period" ) );
        
            
    }
    
    public  boolean getIsEdited(){
        return isEdited;
    }
    
    private void repairRequistState(){
            if (ChekMenuItemReguest.isSelected())
                modbus.startReguisition();
            else
                modbus.stopReguisition();
    }
    private URL url;
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        Logger.getLogger(FXMLController.class.getName()).setLevel( Level.OFF );
            this.url = url;
            PrintStream defaultOut = System.out;
            OutputStream out = new OutputStream() {
 
                 @Override
                public void write( byte b[], int off, int len ) throws IOException {
                    final String s = new String(b, off, len);
                     textAreaLog.appendText(s); 
                    if( textAreaLog.getText().length() > 10000 )
                        textAreaLog.deleteText(0, textAreaLog.getText().length()-5001 );

                    defaultOut.append(s);
                 }     

                @Override
                public void write(int b) throws IOException {
                   
                        write( new byte[] {(byte) b}, 0, 1 );
                    }
                
            };
            System.setOut(new PrintStream(out, true));
          //  System.setErr(new PrintStream(out, true));
            
       
            treeTableView.setRowFactory(ttv -> {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem menuItemDel = new MenuItem( "Удалить" );
                MenuItem menuItemWord = new MenuItem( "Добавить" );
                MenuItem menuItemProperty = new MenuItem( "Параметры" );
                MenuItem menuItemSetUnixTime = new MenuItem( "Передать UNIX Time" );
                MenuItem menuItemSetDefaultVal = new MenuItem( "Передать уставку" );
                MenuItem menuItemChart = new MenuItem( "Тренд..." );
                contextMenu.getItems().addAll(menuItemWord, menuItemProperty, menuItemSetUnixTime, menuItemDel, menuItemSetDefaultVal, menuItemChart );
                TreeTableRow<MbItem> row = new TreeTableRow<MbItem>() {
                    @Override
                    public void updateItem(MbItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setContextMenu(null);
                        } else {
                            // configure context menu with appropriate menu items,
                            // depending on value of item
                            if( item.getMap() == null ){
                                contextMenu.getItems().remove(menuItemProperty);
                                contextMenu.getItems().remove(menuItemChart);                                
                            }
                            if( item.getRawDefaultValue() == null ){
                                 contextMenu.getItems().remove( menuItemSetDefaultVal );
                            }
                            if( item.getType() == null || ! item.getType().equals( MbItem.Type.uint32 )){
                                 contextMenu.getItems().remove(menuItemSetUnixTime );
                            }
                            if( item.getType() != null && item.getType().equals( MbItem.Type.bits ) )
                                contextMenu.getItems().remove( menuItemChart ); 
                            setContextMenu(contextMenu);
                        }
                    }
                };
                
                menuItemChart.setOnAction(evt -> {
               
                    try {
                        MbItem item = row.getItem();
           
                        if( item == null )
                            return;
                             if( item.addDataIsSet() ){
                                 item.getStage().centerOnScreen();
                                 item.getStage().toFront();
                                 return;
                             }     
                        Stage stage = new Stage();
                        Parent root;
                        FXMLLoader  loader = new FXMLLoader( this.getClass().getResource("/fxml/Chart.fxml") );    
                        
                        root = (Parent) loader.load();
                        
                        ChartController cntrl = ( ChartController )loader.getController();
                        item.setAddData(cntrl::addData, stage);
                        stage.setScene(new Scene(root));
                        stage.setTitle(item.getName());
                        
                        stage.initModality(Modality.NONE);
                        stage.initOwner( this.treeTableView.getScene().getWindow());
                        stage.setOnCloseRequest((WindowEvent event) -> {
                           item.setAddData(null, null); 
                        });
                        stage.show();
                       
                    } catch (IOException ex) {
                        
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
                    }
                }
                );
                menuItemDel.setOnAction(evt -> {  
                    
                   TreeItem item = row.getTreeItem();
                    MbItem mbItem = (MbItem) item.getValue();
                    if(mbItem != null && mbItem.getStage() != null )
                        mbItem.getStage().close();
                   item.getChildren().clear();
                   item.getParent().getChildren().
                           remove(item);
                   isEdited = true;
                });
                menuItemWord.setOnAction(evt -> {
                    TreeItem rootItem = row.getTreeItem(),
                             treeItem = new MbItem( "Новый элемент", ((MbItem)(rootItem.getValue())).getServer() , MbItem.Access.RW, 0, MbItem.Type.uint16, MbItem.Func.x3  ).getTreeItem();
                    rootItem.getChildren().add( treeItem );
                    isEdited = true;
                });
                menuItemSetDefaultVal.setOnAction(evnt->{                   
                    try {
                        MbItem code = MbItem.code.get(row.getItem().getServer());
                        if(code != null )
                            this.modbus.getModbusMaster().writeSingleRegister( code.getServer(), code.getAddr(), code.getRawDefaultValue() );  
                        int val = row.getItem().getValueRaw();
                        switch(row.getItem().getType()){
                      
                            case uint32:
                            case int32:
                            case float32:
            
                              int[] reg = {val>>16,val & 0x0000FFFF};
                              modbus.getModbusMaster().writeMultipleRegisters(row.getItem().getServer(), row.getItem().getAddr(), reg);
                              break;
                            default: 
                                modbus.getModbusMaster().writeSingleRegister(row.getItem().getServer(), row.getItem().getAddr(), val);
                        }

                    
                        repairRequistState();
                        isEdited = true;
                    } catch ( ModbusProtocolException | ModbusNumberException | ModbusIOException ex ) {
                        
                        System.out.println( "INFO:Не удалось передать уставку для: "+ row.getItem().getName());
                        Logger.getLogger(FXMLController.class.getName()).log(Level.INFO, "Не удалось передать уставку " + ex.getLocalizedMessage());
                    }

                 });
                menuItemProperty.setOnAction(evt -> {
                    
          
                    try {
                        //TreeItem treeItem = row.getTreeItem();
                        //  treeItem.getChildren().add(new TreeItem(new MbItem("Новый элемент","",0,"word")));

                        MbItem item = row.getItem();//((TreeTableRow<MbItem>)(((MenuItem)(evt.getSource())).getUserData())).getItem();
                        
                        if( item == null )
                            return;
                             
                        Stage stage = new Stage();
                        Parent root;
                        FXMLLoader  loader = new FXMLLoader(this.getClass().getResource("/fxml/EditForm.fxml"));    
                        
                        root = (Parent) loader.load();
                        
                        EditFormController cntrl = (EditFormController)loader.getController();
                            cntrl.setData(item, modbus, period);
                        stage.setScene(new Scene(root));
                        stage.setTitle("Параметры");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner( this.treeTableView.getScene().getWindow());
                        stage.setOnCloseRequest((WindowEvent event) -> {
                            
                            isEdited |=  ((EditFormController)loader.getController()).getIsEdited();
                            this.repairRequistState();
                            this.treeTableView.refresh();
                        });
                        stage.show();
                    } catch (IOException ex) {
                        
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
                    }
                                          
                    
                });
                menuItemSetUnixTime.setOnAction(evt -> {
                    long epochSecond = Instant.now().getEpochSecond();                    
                    int[] reg = new int[2];
                    reg[0] = (int) epochSecond >> 16;
                    reg[1] = (int) (epochSecond & 0x0000FFFF);
                    try {
                        MbItem code = MbItem.code.get(row.getItem().getServer());
                        if(code != null )
                            this.modbus.getModbusMaster().writeSingleRegister( code.getServer(), code.getAddr(), code.getRawDefaultValue() );  
                        modbus.getModbusMaster().writeMultipleRegisters(row.getItem().getServer(), row.getItem().getAddr(), reg);
                        repairRequistState();
                        isEdited = true;
                    } catch (ModbusProtocolException | ModbusNumberException | ModbusIOException ex) {
                        System.out.println( "INFO:Не удалось установить timestamp: " + epochSecond  );
                        Logger.getLogger(FXMLController.class.getName()).log(Level.INFO, "Не удалось установить " + epochSecond + ' ' + ex.getMessage());
                    }
                     
                });
               
                return row;
            });
            
            treeTableColumnName.setCellValueFactory(new TreeItemPropertyValueFactory<MbItem, String>( "name" ));
            
            treeTableColumnName.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
            treeTableColumnName.setOnEditStart(new EventHandler<CellEditEvent<MbItem, String>>(){
                @Override
                public void handle(CellEditEvent<MbItem, String> event) {
                  // event.getRowValue().getValue().setIsEdit(true);
                   modbus.stopReguisition();
                }
            });
            treeTableColumnName.setOnEditCancel(new EventHandler<CellEditEvent<MbItem, String>>(){
                @Override
                public void handle(CellEditEvent<MbItem, String> event) {
                 // clearIsEdit(treeTableView.getRoot());
                    repairRequistState();
                   
                }
            });
            treeTableColumnName.setOnEditCommit(new EventHandler<CellEditEvent<MbItem, String>>(){
                @Override
                public void handle(CellEditEvent<MbItem, String> event) {
                    event.getTreeTableView().getEditingCell().getTreeItem().getValue().setName(event.getNewValue());
                    //event.getRowValue().getValue().setIsEdit(false);
                                     // clearIsEdit(treeTableView.getRoot());
                 repairRequistState();
                }
            } );
            
            treeTableColumnAddr.setCellValueFactory(new TreeItemPropertyValueFactory<>("addr"));
            
            treeTableColumnAddr.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new StringConverter<Object>() {
                @Override
                public String toString(Object object) {
                    return object.toString();
                }
                
                @Override
                public Number fromString(String string) {
                    try{
                        int parseUnsignedInt = Integer.parseUnsignedInt(string);
                        if (parseUnsignedInt >=  -MbItem.addrCorrect )
                            return parseUnsignedInt;
                        else{
                            System.out.println( "INFO:Некоректный адрес < " + -MbItem.addrCorrect );
                            throw new NumberFormatException("Некоректный адрес < " + -MbItem.addrCorrect);
                        }
                    }catch(NumberFormatException ex){
                        System.out.println("Некоректный адрес: " + -MbItem.addrCorrect);
                        ex.setStackTrace( new StackTraceElement[0]);
                         throw ex;  
                         }     
                }                
            }));
            
        treeTableColumnAddr.setOnEditStart((CellEditEvent<MbItem, Object> event) -> {   
           modbus.stopReguisition();
        }); 
        treeTableColumnAddr.setOnEditCancel((CellEditEvent<MbItem, Object> event) -> { 
          repairRequistState(); 
        });           
        treeTableColumnAddr.setOnEditCommit((CellEditEvent<MbItem, Object> event) -> {
            synchronized (mbReguestData){
                
                
                
                
                isEdited = true;
                if (event.getRowValue().getValue().getMap() == null) {
                    event.getTreeTableView().getEditingCell().getTreeItem().getValue().setServer((int) (event.getNewValue()));
                } else {
                    event.getTreeTableView().getEditingCell().getTreeItem().getValue().setAddr((int) (event.getNewValue()));
                }
            }
            repairRequistState();
        });
       
        
        
        treeTableColumnType.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        treeTableColumnType.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(FXCollections.observableArrayList(FXCollections.observableArrayList(MbItem.Type.values()))));
        treeTableColumnType.setOnEditStart((CellEditEvent<MbItem, Object> event) -> {
            MbItem value = event.getRowValue().getValue();
            if (value == null || value.getType() == null ){ 
                event.getTreeTableView().edit(0, null);
            }else
            modbus.stopReguisition();
        });
        treeTableColumnType.setOnEditCancel((CellEditEvent<MbItem, Object> event) -> {
          repairRequistState();
        });        
        treeTableColumnType.setOnEditCommit((CellEditEvent<MbItem, Object> event) -> {
            event.getRowValue().getValue().setType(MbItem.Type.valueOf(event.getNewValue().toString()));
            repairRequistState();
        });

        this.treeTableColumnVolue.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        this.treeTableColumnVolue.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new StringConverter<Object>() {
            @Override
            public String toString(Object object) {
                if (object == null) {
                    return null;
                }
                if(object instanceof SimpleStringProperty)
                return ((SimpleStringProperty)object).getValue();
                return null;
            }

            @Override
            public Object fromString(String string) {

                MbItem mbItem = (MbItem) (treeTableView.getEditingCell().getTreeItem().getValue());

                int registers[] = new int[2];
                int val = 0;
                try {
                     switch (mbItem.getType()) {
                        case bytes:
                            String[] split = string.split("\\D", 2);
                            if (split.length > 1) 
                                val = Integer.parseInt(split[0])<<8 |  Integer.parseInt(split[1]); 
                            else 
                                val = Integer.parseInt(split[0]);
                            modbus.getModbusMaster().writeSingleRegister(mbItem.getServer(), mbItem.getAddr(), val);
                            return val;
                        case int32:
                        case uint32:
                            val = Integer.parseInt(string);
                            registers[0] = (int) (val << 16);
                            registers[1] = (int) (val & 0x00FF);
    
                                modbus.getModbusMaster().writeMultipleRegisters(mbItem.getServer(), mbItem.getAddr(), registers);
                            return val;
                        case float32:
                            val = ((int) (Double.doubleToRawLongBits(Float.parseFloat(string))));
                            registers[0] = val >> 16;
                            registers[1] = val & 0x0000FFFF;
      
                                modbus.getModbusMaster().writeMultipleRegisters(mbItem.getServer(), mbItem.getAddr(), registers);
            

                            break;
                        case int16:
                        case uint16:
                            val = Integer.parseInt(string);
                            modbus.getModbusMaster().writeSingleRegister(mbItem.getServer(), mbItem.getAddr(), val);
                            return val;
                           
                        case bits:
                            val = Integer.parseInt(string, 2);
                            if (string.length() > 16) {
                                registers[0] = (int) (val << 16);
                                modbus.getModbusMaster().writeMultipleRegisters(mbItem.getServer(), mbItem.getAddr(), registers);
                            } else {
                               modbus.getModbusMaster().writeSingleRegister(mbItem.getServer(), mbItem.getAddr(), val);
                            }
                            return val;
                    }


                } catch ( NumberFormatException ex ) {
                    System.out.println( "Недопустимое значение: "+ string );
                    ex.setStackTrace(new StackTraceElement[0]);
                    throw ex;
                } catch (ModbusProtocolException | ModbusNumberException | ModbusIOException ex) {
                    System.out.println("INFO:Записать значение для: "+ mbItem.getName()+ " не удалось!");
                    Logger.getLogger(FXMLController.class.getName()).log(Level.INFO, "Записать не удалось! " + ex.getMessage());
                }
                return mbItem.getRepresentationOfValue();
            }
        }));
        this.treeTableColumnVolue.setOnEditStart((CellEditEvent<MbItem, Object> event)  -> { 
                MbItem value = event.getRowValue().getValue();
            if (value == null || value.getType() == null ){ 
                event.getTreeTableView().edit(0, null);
            }else
            modbus.stopReguisition();
        });
        this.treeTableColumnVolue.setOnEditCommit((CellEditEvent<MbItem, Object> event)  -> { 
           repairRequistState();
        });
        this.treeTableColumnVolue.setOnEditCancel((CellEditEvent<MbItem, Object> event)  -> { 
           repairRequistState();
        });
        treeTableColumnBar.setCellValueFactory(new TreeItemPropertyValueFactory<>("bar"));/*
        new Callback<TreeTableColumn.CellDataFeatures<MbItem,Object>, ObservableValue<Object>>(){
                @Override
                public ObservableValue<Object> call(TreeTableColumn.CellDataFeatures<MbItem, Object> param) {
                   return  new SimpleObjectProperty<>( param.getValue().getValue().getBarOrDefaultValue());
                }
            });*/
        treeTableColumnDefaultValue.setCellValueFactory(new TreeItemPropertyValueFactory<>("defaultValue"));
        treeTableColumnDefaultValue.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new StringConverter<Object>() {
                @Override
                public String toString( Object object ) {
                   if( object != null ){
                       return  (String) object;
                   }
                   return null;
                }

                @Override
                public Object fromString( String string ) {
                     MbItem mbItem = (MbItem) (treeTableView.getEditingCell().getTreeItem().getValue());
                        if(string.isEmpty()){ 
                             mbItem.setDefaultValue(null);
                             return null;
                        }
                     try{
                         Integer val;
                         switch(mbItem.getType()){
                             case bits: 
                                 val = Integer.parseUnsignedInt(string, 2);
                                 break;
                             case float32: 
                                val  = Float.floatToRawIntBits(Float.parseFloat(string));
                                break;
                             default: 
                               val =  Integer.parseInt(string);
                         }
                         if(string.isEmpty()) 
                             mbItem.setDefaultValue(null);
                         mbItem.setDefaultValue(val);
                         return string;
                     }catch(NumberFormatException ex){
                       System.out.println( "Недопустимое значение: "+ string );
                       ex.setStackTrace(new StackTraceElement[0]);
                       throw ex;
                      }
                     
         
                }
            
        }));
               this.treeTableColumnDefaultValue.setOnEditStart((CellEditEvent<MbItem, Object> event)  -> { 
                MbItem value = event.getRowValue().getValue();
            if (value == null || value.getType() == null ){ 
                event.getTreeTableView().edit(0, null);
            }else
            modbus.stopReguisition();
        });
        this.treeTableColumnDefaultValue.setOnEditCommit((CellEditEvent<MbItem, Object> event)  -> { 
           repairRequistState();
        });
        this.treeTableColumnDefaultValue.setOnEditCancel((CellEditEvent<MbItem, Object> event)  -> { 
           repairRequistState();
        });
        
        treeItemRoot = new TreeItem();
        treeTableView.setShowRoot(false);
        treeTableView.setRoot(treeItemRoot);
        treeTableView.setEditable(true);
            
            // TODO

    }
    public  Gson gson = new GsonBuilder().setPrettyPrinting()
                                     .registerTypeAdapter( TreeItem.class, new TreeItemSerializer())
                                     .registerTypeAdapter( TreeItem.class, new TreeItemDeserializer())
                                     .create();

    private static final SerialParameters serialParameters = new SerialParameters("COM1",SerialPort.BaudRate.BAUD_RATE_115200,8,2,SerialPort.Parity.NONE);
    
    public static SerialParameters getSerialParameters(){
        return serialParameters;
    }
    
    private Modbus modbus = new Modbus(this);
    private String modbusMasterType = "RTU";
    
    public String getModbusMasterType(){
        return modbusMasterType;
    }
    @FXML
    private void linkParamOnAction(ActionEvent event) throws IOException {
                    Stage stage = new Stage();
                    Parent root;
                    FXMLLoader  loader = new FXMLLoader(this.getClass().getResource("/fxml/linkSetup.fxml"));                  
                   
                    root = (Parent) loader.load();
                    LinkSetupController cntrl = loader.getController();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Параметры связи");
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner( this.treeTableView.getScene().getWindow());
                    stage.setOnShowing(evnt->{
                        cntrl.setData( serialParameters, modbusMasterType, this.period );
                     
                    });
                    cntrl.setBtnSaveAction((ActionEvent actionEvent) -> {
                        try {
                            modbusMasterType = cntrl.getType();    
                            modbus.newMaster(                       
                                    cntrl.getSerialParameters(), cntrl.getType() );
                            properties.setProperty("port", serialParameters.getDevice());
                            properties.setProperty("BaudRate", Integer.toString(serialParameters.getBaudRate()) );
                            properties.setProperty("DataBits", Integer.toString(serialParameters.getDataBits()) );
                            properties.setProperty("Parity", serialParameters.getParity().name());
                            properties.setProperty("StopBits", Integer.toString(serialParameters.getStopBits() ));
                            if(cntrl.getPeriod() != null)
                                properties.setProperty("Period", cntrl.getPeriod().toString());
                            MbItem.addrCorrect = cntrl.getAddrCorect();
                            this.treeTableView.refresh();
                        } catch (ModbusIOException | SerialPortException ex) {
                            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
                        }
                        stage.close();
                    });
                    cntrl.setBtnCancelAction((ActionEvent actionEvent) -> {
                        stage.close();
                    });
                    

                    stage.initStyle(StageStyle.UTILITY);
                    stage.show();
        
    }

    @FXML
    private void reguestOnAction(ActionEvent event) {
        if(this.ChekMenuItemReguest.isSelected()){
          ChekMenuItemReguest.setSelected(this.modbus.startReguisition());
          
        }else
           this.modbus.stopReguisition();
    }
    
    private File openedFile;
    public File getOpenedFile(){
        return openedFile;
    }
    public TreeItem getRoot(){
        return this.treeTableView.getRoot();
    }
    private void closeAllChart(TreeItem treeItem)
    {
        MbItem mbItem = (MbItem)(treeItem.getValue());
        
        if ( mbItem != null &&  mbItem.getStage() != null ){
            mbItem.getStage().close();
        }
       treeItem.getChildren().forEach(  each->{
            closeAllChart((TreeItem) each);
            });   
    }
    @FXML
    private void menuItemOpenOnAction(ActionEvent event) throws  URISyntaxException, MalformedURLException {
        FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter(
            "JSON files (*.json)", "*.json"));
            
        fileChooser.setInitialDirectory(new File(".").getAbsoluteFile());
        openedFile = fileChooser.showOpenDialog(treeTableView.getScene().getWindow());
        if (openedFile != null ){
            try {
                JsonReader  jreader = new JsonReader ( new FileReader(openedFile) );
                this.treeItemRoot = gson.fromJson(jreader, TreeItem.class);
                closeAllChart(this.getRoot());
                this.treeTableView.setRoot(treeItemRoot);
                setTitle.accept(openedFile.getName());

            } catch (FileNotFoundException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING,"Не удалось откыть...");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        } 
            
    }

    @FXML
    private void menuItemSaveOnAction(ActionEvent event) throws  URISyntaxException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter(
            "JSON files (*.json)", "*.json"));
        fileChooser.setInitialDirectory(openedFile != null?openedFile.getParentFile():new File(".").getAbsoluteFile());
        File f = fileChooser.showSaveDialog(treeTableView.getScene().getWindow());
        if( f != null )
        try {
            f.createNewFile();
            FileWriter writer = new FileWriter(f);  
            writer.write( gson.toJson(treeItemRoot) );//writer.write(treeItemdata2GsonObject.gson.toJson(jSonObject));
            writer.flush();
            this.openedFile = f;
            this.setTitle.accept(f.getName());
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING,"Сохранить не удалось...");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            //Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

         
    }

    @FXML
    private void treeTableViewOnMouseClicked(MouseEvent event) throws InterruptedException {
     
     
    }
    private int[] getVisibleRange(TreeTableView table) {
        TreeTableViewSkin skin = (TreeTableViewSkin) table.getSkin();
        if (skin == null) {
            return new int[]{0, 0};
        }
        VirtualFlow<?> flow = (VirtualFlow) skin.getChildren().get(1);
        int indexFirst;
        int indexLast;
        if (flow != null && flow.getFirstVisibleCellWithinViewPort() != null
                && flow.getLastVisibleCellWithinViewPort() != null) {
            indexFirst = flow.getFirstVisibleCellWithinViewPort().getIndex();
         //   if (indexFirst >= table.getTreeItem(indexFirst).size()) {
         //       indexFirst = table.getItems().size() - 1;
        //    }
            indexLast = flow.getLastVisibleCellWithinViewPort().getIndex();
       //     if (indexLast >= table.getItems().size()) {
      //          indexLast = table.getItems().size() - 1;
        //    }
        } else {
            indexFirst = 0;
            indexLast = 0;
        }
        return new int[]{indexFirst, indexLast};
    }
    

    private final Map<Integer, Map<Integer, List<MbItem>>> mbReguestData = new ConcurrentHashMap();   
    
    public synchronized  Map<Integer, Map<Integer,List<MbItem>>> getVisibleMbItem( Boolean update ){
 
      if (!update) return mbReguestData;

     Platform.runLater(new Runnable(){
           @Override
         public void run() {
//     synchronized (mbReguestData){            
     int[] range = getVisibleRange(treeTableView);
     int i = range[1];
     TreeItem item = treeTableView.getTreeItem(i);
         mbReguestData.forEach((key,val)->{ 
         val.clear(); 
            });
     while( (item != null) && (i > range[0]) ){
        if (item.getValue() == null )
        {
            --i;
            continue; 
        }
        
        int addr = ((MbItem)(item.getValue())).getServer() != null?((MbItem)(item.getValue())).getServer():0;
        int func = ((MbItem)(item.getValue())).getFunc().getCode();
         /*System.out.printf(((MbItem)(treeTableView.getTreeItem(i).getValue())).getName()+'\n');
          do{
             addr = item.getParent().equals(this.treeTableView.getRoot()) ? ((MbItem)(item.getValue())).getAddr():addr;
             func = (func != 0) ? func : ((MbItem)(item.getValue())).getFunc();
             item = item.getParent();
          }while((addr == 0 || func == 0 )&& item != null &&item.getValue()!= null );
          */
         
          Map<Integer,List<MbItem>> map = new ConcurrentHashMap(),
                  res =  mbReguestData.putIfAbsent(func,  map);
          map = (res != null) ? res:map;
          item = treeTableView.getTreeItem(i--);
          List list = new ArrayList(),
            curList = map.putIfAbsent(addr, list);
          curList = (curList == null) ? list : curList;
          if(((MbItem)(item.getValue())).getMap() != null)
            curList.add( item.getValue() );
        }
      //  mbReguestData.notify();
        }
  //      }  
         
     });
    //    treeItem  = treeTableView.getTreeItem(range[0]);
    
        addChartsToMbReguestData(treeTableView.getRoot());
        return mbReguestData;
    }
    private void addChartsToMbReguestData( TreeItem treeItem ){
        MbItem mbItem = (MbItem)(treeItem.getValue());
        
        if (mbItem != null && mbItem.getMap()!= null && mbItem.addDataIsSet()){
            int addr = mbItem.getServer() != null?mbItem.getServer():0;
            int func = mbItem.getFunc().getCode();
              Map<Integer, List<MbItem>> map = new HashMap(),
              res =  mbReguestData.putIfAbsent( func,  map );
              map = (res != null) ? res:map;
              List list = new ArrayList(),
              curList = map.putIfAbsent( addr, list );
              curList = (curList == null) ? list : curList;
              curList.add( mbItem );
        }
            treeItem.getChildren().forEach(  each->{
                addChartsToMbReguestData((TreeItem) each);
            });   
        
    }
    private int period = 1000;
    public int getPeriod(){
       return period;
   }

           
    @FXML
    private void treeTableViewOnScrollFinisghed(ScrollEvent event) {
        
    }

    @FXML
    private void treeTableViewOnSwipeDown(SwipeEvent event) {
    }

    @FXML
    private void treeTableViewOnSwipeUp(SwipeEvent event) {
    }

    @FXML
    private void treeTableColumnAddrOnEditStart(CellEditEvent<?,? > event) {
        
    }

    @FXML
    private void textAtiaLogMenuItemCliarOnAction(ActionEvent event) {
          textAreaLog.clear();
    }
    private void clearIsEdit( TreeItem<MbItem> item){
        MbItem value = item.getValue();
        if ( value != null )
            value.setIsEdit(false);
        item.getChildren().forEach(each->{
         clearIsEdit(each);   
        });
    }
    public void setFsetTitle(Consumer<String> setTitle){
        this.setTitle = setTitle;
    }
   private void setDefaultValues(TreeItem item) {
        MbItem value = (MbItem) item.getValue();
        if( value != null ) 
            if(value.getRawDefaultValue() != null){
                try {
                    MbItem code = MbItem.code.get(value.getServer());
                        if(code != null && !code.equals(item) )
                            this.modbus.getModbusMaster().writeSingleRegister( code.getServer(), code.getAddr(), code.getRawDefaultValue() );    
                        this.modbus.getModbusMaster().writeSingleRegister( value.getServer(), value.getAddr(), value.getRawDefaultValue() );
                    } catch ( ModbusProtocolException | ModbusNumberException | ModbusIOException ex ) {
                    System.out.println("INFO:Не удалось установить уставку для: "+ value.getName() +"!" );
                    Logger.getLogger(FXMLController.class.getName()).log( Level.SEVERE, "Не удалось установить уставку:" + value.getName() + ex.getMessage() );
                }
            }
        item.getChildren().forEach( each->{
            setDefaultValues((TreeItem) each);
        });
    } 
    
    @FXML
    private void menuItemSetDefault( ActionEvent event ) {
       setDefaultValues(this.treeTableView.getRoot()); 
       repairRequistState();
       
       
    }
    

    
    @FXML
    private void menuItemAbout( ActionEvent event ) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "unifyModbusClient");
            alert.setTitle("О программе");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add( new Image( getClass().getResourceAsStream( "/logo.png" )) );
            alert.setHeaderText("unifyModbusClient " + Version.BUILD_NUMBER );
            alert.setContentText("ООО НТФ \"Микроникс\"\n alekseev@mx-omsk.ru\n (программа пока поддерживает только груповой опрос, 'дырявые устройства' доступны толко на запись...  )" );
            alert.showAndWait();
    
    }
}    