/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import static com.micronix.unifymodbusclient.MbItem.Type.float32;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;

/**
 *
 * @author alekseev
 */
public class MbItem {
    public static int addrCorrect = 0;   
    public final static Map<Integer,MbItem> code = new HashMap();
       
      enum Func {
        x3("0x03 Read Holding Registers",3 );
        //x6("0x06 Preset Single Register",6),
       // x10("0x10(16) Preset Multiple Registers",16);
        private String description;
        private int code;
        private Func(String desc, int code){
            description = desc;
            this.code = code;
        }
        public String getDescription() {
            return description;
        }
        public int getCode(){
            return code;
        }
        Func getByCode(int code){
            for( Func mbFunc : Func.values()){
                if( mbFunc.getCode() == code ) 
                 return  mbFunc;
            }
            return null;
        }
       
    }
    enum Type{
        int16,
        int32,
        uint16,
        uint32,
        float32,
        bits,
        bytes
        
        
    }
    
    enum Access{
        R,
        W,
        RW
    }
      
    private String name;
    private Access access;
    private Integer addr,server;
    private Type type; 
    private int point;
    private Object barOrDefaultValue;
    private Map<Integer, String>  map;
    private Func func ;
    private boolean hex = false;
    private  SimpleFloatProperty min = null, max = null;  
    private SimpleIntegerProperty  value = null;
    private  Consumer<MbItem> addData; 
    private final SimpleStringProperty representationOfValue = new SimpleStringProperty();
    private TreeItem treeItem;
    private boolean isEdit = false;
    
    public MbItem(MbItem item){
        this(item.name,item.server,item.access,item.addr,item.type,item.func,item.getRawDefaultValue(),item.getMin(),item.getMax());
        this.setMap((HashMap) item.getMap());
     }
    public MbItem( String name, int server, Access access, int addr, Type type, Func func )
    {

        this( name, server, access, addr, type, func, null, null , null);
    }
    
    public MbItem( String name, int server, Access access, int addr, Type type,Func func, Integer defaultValue, Float min, Float max ){
        this.server = server;
        this.setName(name);
        this.access = access;
        this.addr = addr;
        this.type = type;
        this.func = func;
        this.map =  new HashMap();

        if(defaultValue != null)
            barOrDefaultValue =  defaultValue;
        //else 
          //  barOrDefaultValue =  new ProgressBar(0.5);
          this.setMin(min);
          this.setMax(max);
        this.treeItem = new TreeItem(this);
          MbItem self = this;
        ChangeListener<String> listener = (obs, oldName, newName) -> {
        Platform.runLater(new Runnable(){
         @Override
           public void run() {
              if (! self.isEdit  ){
                TreeModificationEvent<MbItem> event = new TreeModificationEvent<MbItem>( TreeItem.valueChangedEvent(), self.treeItem );
                Event.fireEvent(self.treeItem, event);
              }
           }
           
     });
    };
        representationOfValue.addListener(listener);
        value = null;
     
     }
    
    
    public MbItem( String name, int server, Func func  )
    {
        this.name = name;
        this.server = server;
        this.func = func;
        this.access = Access.RW;
        this.treeItem = new TreeItem(this);
         value = null;
    }
    
    public TreeItem getTreeItem(){
        return this.treeItem;
    }
    public String getName(){
        return name;
    }
    
    public void setName( String name ){
        this.name = name; 
        if(min == null || max == null || code.get(server) == null)
        switch( name.toLowerCase().trim() ){
            case "max":
             ((MbItem)(this.treeItem.getParent()).getValue()).setMax((float)0);
             ((MbItem)(this.treeItem.getParent()).getValue()).max.bind(value);
             break;
            case "min":
             ((MbItem)(this.treeItem.getParent()).getValue()).setMin((float)0);
             ((MbItem)(this.treeItem.getParent()).getValue()).min.bind(value); 
             break;  
            case "ключ доступа":
            case "код доступа":
                 code.put(server, this);
        }
    }
    
    public Access getAccess(){
        return access;
    }
    
    public void setAccess( Access access ){
        this.access = access;    
    }
    public Integer getServer(){
         return server;
    } 
    public void setServer(int addr){
        server = addr;
    }
    public Integer getAddr(){
        if (addr == null){
            return server;
        }
        return addr;
    }
    
    public void setAddr( int addr ){
        
        this.addr = addr;    
    }
    
    public Type getType(){
        return type;
    }
    
    public void setType( Type type ){
        this.type = type;    
    }
    
    public Integer getPoint(){
        return point;
    }
    
    public void setPoint( int point ){
        this.point = point;    
    }
    
    public Object getBarOrDefaultValue(){
         if (barOrDefaultValue instanceof Integer){
             switch(getType()){
                case bits:
                    return Integer.toBinaryString((Integer)barOrDefaultValue);
                case float32: 
                    return Float.intBitsToFloat((Integer)barOrDefaultValue);
                
             }
         }   
        return this.barOrDefaultValue;
    }
    
    public Object getBar(){
        if (this.barOrDefaultValue instanceof ProgressBar)
            return this.barOrDefaultValue;
        else
            return null;
    }
    public void setDefaultValue( Integer defaultValue ){
        this.barOrDefaultValue = defaultValue; 
        if(defaultValue == null && min != null && max != null)
             this.barOrDefaultValue = new  ProgressBar(0.5);
    }
    public Integer getRawDefaultValue(){
        if( this.barOrDefaultValue instanceof Integer )
            return (Integer) barOrDefaultValue;
        else
            return null;
    }
        public String getDefaultValue(){
          if( this.barOrDefaultValue instanceof Integer )
              switch(this.getType()){
                  case bits: 
                      return Integer.toBinaryString((int) barOrDefaultValue);
                  case float32:
                      return Float.toString(Float.intBitsToFloat((int) barOrDefaultValue));
                  default:
                    return Integer.toString((int) barOrDefaultValue);
                            
              }
           
          return null;
    }   
    
    public Map getMap(){
        return map;
    }   
    
    public void setMap( HashMap map ){
        this.map = map;    
    }
    
    public void setValue( Integer value ){
        
      if(value != null)  
      if ( this.value == null)  
          this.value = new SimpleIntegerProperty(value);
      else 
          this.value.set(value);
    
      if(this.addData != null) 
          this.addData.accept(this);
      Platform.runLater(new Runnable(){
          @Override
          public void run() {          
            getValue();  
          }
      });
        
     }
    
    public Integer getValueRaw(){    
        if(this.value != null) 
            return this.value.getValue();
        else
            return null;
    }
    
    public SimpleStringProperty getValue(){
 
        if ( this.value == null ) return this.representationOfValue;
        Integer val = this.value.get();

    
        switch( this.type ){
            case bytes:
                if(this.hex)
                     this.representationOfValue.set(Integer.toHexString(val >> 8)+";" + Integer.toHexString(val & 0x000000FF));
                this.representationOfValue.set(Integer.toHexString(val >> 8)+";" + Integer.toHexString(val & 0x000000FF));
                return this.representationOfValue;
                
            case uint32:
            case uint16: 
              val = Math.abs( val );
            case int32:
            case int16: 
                String mapVal = this.map.get(val.intValue());
                 if ( mapVal != null ) {
                     this.representationOfValue.set(mapVal);
                     
                     return this.representationOfValue;
                    }
                 if(this.barOrDefaultValue != null && this.barOrDefaultValue instanceof ProgressBar ){
                    ((ProgressBar)barOrDefaultValue).setProgress( val.floatValue()/(max.get()-min.get()) );
                 }  
                if(this.point == 0 ){
                 this.representationOfValue.set(val.toString());
                
                 return this.representationOfValue;      
                }
                    
            case float32:
               float f = (float) (( !this.type.equals( MbItem.Type.float32 ) ) ?( val / ( 10.0 * this.point + ((this.point == 0) ? 1 : 0) )) : (Float.intBitsToFloat( val )));

               if( this.barOrDefaultValue instanceof ProgressBar ){
                 ((ProgressBar)barOrDefaultValue).setProgress(f/ (max.get()-min.get()) );
               }
              
               if(this.hex)
                    this.representationOfValue.set(Float.toHexString(f));
               else
                   this.representationOfValue.set(Float.toString(f));
                return this.representationOfValue;
              
            case bits:
                String s ="";
                for(int i = 0; i < 32; ++i  ){
                 mapVal = this.map.get(val & (1 << i));
                 if ( mapVal != null ) {
                    s += mapVal+'\n';
                    }
                }
                s = s.trim();
                if (s.isEmpty())
                 this.representationOfValue.set(Integer.toBinaryString(val));
                else
                 this.representationOfValue.set( s );
                return representationOfValue;
        }
        return null;              
    }
    public void setFunc(Func value){
        this.func = value;
    }
    public Func getFunc(){
        return this.func;
    }
    public void setHex(boolean hex){
        this.hex = hex;
    }
    public  Float getMax(){
        if( max != null )
            return this.max.getValue();
        return null;
    }
    public final void setMax(Float max){
        if (max == null)
            this.max = null;
        else
           if( this.max == null )
             this.max = new SimpleFloatProperty(max);
           else
            this.max.set(max);
        if( this.barOrDefaultValue == null && min != null && max != null)
         this.barOrDefaultValue = new  ProgressBar(0.5);
    }
    public Float getMin(){
        if( min != null )
            return this.min.getValue();
        return null;
    }
    public final void setMin(Float min){
        if (min == null)
            this.min = null;
        else
           if( this.min == null )
             this.min = new SimpleFloatProperty(min);
            else
                this.min.set(min);
        if( this.barOrDefaultValue == null && min != null && max != null)
            this.barOrDefaultValue = new  ProgressBar(0.5);
    }    


    public SimpleStringProperty getRepresentationOfValue(){
        return this.representationOfValue;
    }
    public void setIsEdit(boolean isEdit){
        this.isEdit = isEdit;
    }
    public boolean getIsEdit(){
     return this.isEdit;   
    }
     public boolean addDataIsSet( ){
      return this.addData != null;
    }
    public void setAddData(Consumer<MbItem> addData  ){
        this.addData = addData;
    }
}

