/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortException;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alekseev
 */
public class Modbus {

    private ModbusMaster master;
    private Thread thread;
    private FXMLController controller;
    private int[] getReg(int func, int addr,int reg, int cnt) throws ModbusProtocolException, ModbusNumberException, ModbusIOException{
    
        switch(func){
            
            case 3: return master.readHoldingRegisters(addr, reg, cnt);
                
            case 4: return master.readInputRegisters(addr, reg, cnt);
        }
        return null;
    }   
    
    private boolean[] getBits(int func,int addr,int reg, int cnt) throws ModbusProtocolException, ModbusNumberException, ModbusIOException{
        switch(func){
            case 1: return master.readCoils(addr, reg, cnt);
            case 2: return master.readDiscreteInputs(addr, reg, cnt);
        }
        return null;
    }
    
    final Runnable runnable;
     Modbus(FXMLController controller){  
        this.runnable = new Runnable() {
            private boolean linkUp = false;
            @Override
            public void run() {
       
                while (! thread.isInterrupted() ) {
                    
                    try {
                        thread.sleep(controller.getPeriod());
                    } catch ( InterruptedException ex ) {
                        break;
                    }
                    Map<Integer, Map<Integer, List<MbItem>>> reguests = controller.getVisibleMbItem( true );
                    // synchronized (reguest) {
                    // try {
                    // reguest.wait();
                    //  } catch (InterruptedException ex) {
                    //  break;
                    // }
                     
                    reguests.forEach((Integer func, Map<Integer, List<MbItem>> addrs) -> {
                     
                        switch (func) {
                            case 1:
                            case 2:
                                addrs.forEach( (Integer addr, List<MbItem> mbItems) -> {
                                    try{        
                                        for (MbItem mbItem :  mbItems ) {
                                            boolean[] bits = null;                 
                                            if(mbItem.getAddr() >= 0)
                                                switch (mbItem.getType()) {
                                                    case _1bit:
                                                         bits = getBits(func, addr, mbItem.getAddr(), 1 );
                                                        break; 
                                                     case _8bit:
                                                          bits = getBits(func, addr, mbItem.getAddr(), 8 );
                                                         break; 
                                                     case  int32:
                                                     case uint32:
                                                          bits = getBits(func, addr, mbItem.getAddr(), 32 );
                                                    default:
                                                         bits = getBits(func, addr, mbItem.getAddr(), 16 );   
                                            }
                                        mbItem.setValue(bits);
                                        if( !this.linkUp )System.out.println("Связь установлена!");                                     }
                                        this.linkUp = false;     
                                        } catch (ModbusProtocolException | ModbusNumberException | ModbusIOException ex) {
                                        this.linkUp = false;System.out.print("!!!Ошибка связи!");
                                        Logger.getLogger(Modbus.class.getName()).log(Level.SEVERE, ex.getMessage());
                                  }
                                });
                                break;   
                            case 3: 
                               /* addrs.forEach( (Integer addr, List<MbItem> mbItems) -> {
                                   
                                    Object[] toArray = mbItem.toArray();
                                    Arrays.sort(toArray, (Object a, Object b)->{
                                    ((MbItem)a).setValue(null);
                                    ((MbItem)b).setValue(null);
                                    return ((MbItem)a).getAddr().compareTo( ((MbItem)b).getAddr() );
                                    });
                                    // mbItem.sort( (MbItem a, MbItem b) -> {
                                    //     return a.getAddr().compareTo( b.getAddr() );
                                    //  });*/
                                    //   mbItem.forEach((r)->{
                                    //        r.setValue(null);
                                    //   });
                                    // int addrFirst = ((MbItem)toArray[0]).getAddr() + MbItem.addrCorrect;
                                    // int addrLast =((MbItem)toArray[toArray.length-1]).getAddr()+MbItem.addrCorrect; //reg.get( mbItem.size()-1 ).getAddr();
                                    
                                    //  for(; addrFirst < addrLast; addrFirst+= 125)
                                   
                                        //      if(addrLast - addrFirst > 125)
                                        //            addrLast += 125 - addrLast - addrFirst;
                                        //        else
                                        //             addrLast = ((MbItem)toArray[toArray.length-1]).getAddr();
                                        
                                        //int[] reg = master.reg( addr, addrFirst,
                                        //                addrLast - addrFirst + MbItem.addrCorrect ); //групповой запрос
                                        //       if (reg.length >= addrLast - addrFirst + 1)
                                        
                                        
                               case 4: addrs.forEach( (Integer addr, List<MbItem> mbItems) -> {
                             
                               try{
                                   for (MbItem mbItem :  mbItems ) {
               
                                          if(mbItem.getAddr() >= 0)
                                            switch (mbItem.getType()) {
                                                
                                                case int32:
                                                case uint32:
                                                case float32:
                                                    int[] reg = getReg(func ,addr, mbItem.getAddr(), 2 );
                                                    mbItem.setValue(reg[0] << 16 | reg[1] );
                                                    break;  
                                                default:
                                                    reg = getReg(func, addr ,mbItem.getAddr() ,1 );
                                                    mbItem.setValue( reg[ 0 ] );
                                                    
                                            }
                                          
                                     
                                     }
                                   if(!linkUp) System.out.println("Связь установлена!");
                                   linkUp = true;
                                  } catch (ModbusProtocolException | ModbusNumberException | ModbusIOException ex) {
                                        this.linkUp = false;System.out.print("!!!Ошибка связи!");
                                        Logger.getLogger(Modbus.class.getName()).log(Level.SEVERE, ex.getMessage());
                                  }
                                });
                        }

                    });
                    // }
                
                }
            }
        };  
        this.controller = controller; 
        SerialUtils.setSerialPortFactoryJSSC();
        thread = new Thread(runnable  ,"thread-Modbus" ); 
        com.intelligt.modbus.jlibmodbus.Modbus.setLogLevel(com.intelligt.modbus.jlibmodbus.Modbus.LogLevel.LEVEL_RELEASE);    
        
      

    }  
   
    public ModbusMaster newMaster(Object parameters, String type) throws ModbusIOException, SerialPortException {
        if (master != null) {
            master.disconnect();
        }
        
        switch (type){
            case "ASCII" : master =  ModbusMasterFactory.createModbusMasterASCII((SerialParameters)parameters);
            break;
            case "RTU" : master =  ModbusMasterFactory.createModbusMasterRTU((SerialParameters)parameters);
            break;
        }
        master.setResponseTimeout(1000);
     /*   final FrameEventListener listener = new FrameEventListener() {
                @Override
                public void frameSentEvent(FrameEvent event) {
                    System.out.println("frame sent " + DataUtils.toAscii(event.getBytes()));
                }

                @Override
                public void frameReceivedEvent(FrameEvent event) {
                    System.out.println("frame recv " + DataUtils.toAscii(event.getBytes()));
                }
                
        };
        this.master.addListener(listener);
        */
        master.connect();
        return master;
        //this.thread.start();
                
    }
    public boolean startReguisition(){
       if( getModbusMaster() == null) return false;
        if ( this.thread.isAlive()) return true;
        this.thread = new Thread(this.runnable, "thread-Modbus");
        this.thread.setDaemon(true);
        this.thread.setPriority( Thread.NORM_PRIORITY - 1 );
        this.thread.start();
        return true;
    }
    
    public void stopReguisition()  {
        try {
           while(this.thread.isAlive() && ! this.thread.isInterrupted()) 
            this.thread.interrupt();
            
            if (this.master != null)
                this.master.disconnect();
        } catch (ModbusIOException ex) {
            Logger.getLogger(Modbus.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }
    
    
    public ModbusMaster getModbusMaster(){  
        try {
            if (master != null) {
                master.connect();
                return master;
            }
            
            master = newMaster(this.controller.getSerialParameters(), this.controller.getModbusMasterType());
            
            master.connect();
            return master;
        } catch (ModbusIOException | SerialPortException ex) {
             Logger.getLogger(Modbus.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        return null;
    }
    public void writeReg(MbItem item, int value) throws ModbusProtocolException, ModbusNumberException, ModbusIOException{
        if(this.getModbusMaster()== null) return;
         MbItem code = MbItem.code.get(item.getServer());
            if(code != null && !code.equals( item ) )
                this.getModbusMaster().writeSingleRegister( code.getServer(), code.getAddr(), code.getRawDefaultValue() );   
                        
                        switch(item.getType()){ 
                            case _1bit:
                                this.getModbusMaster().writeSingleCoil(item.getServer(),  item.getAddr(), (value != 0) );
                                break;
                            case uint32:
                            case int32:
                            case float32:    
                                    this.getModbusMaster().writeSingleRegister( item.getServer(), item.getAddr(), value >> 16 );
                                    this.getModbusMaster().writeSingleRegister( item.getServer(), item.getAddr() + 1, value & 0x0000FFFF);
                                break;
                            default: this.getModbusMaster().writeSingleRegister( item.getServer(), item.getAddr(), value );
                        }  
    }
    
    
            
}
