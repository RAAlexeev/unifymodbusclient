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

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            while (!Thread.currentThread().interrupted()) {

                try {
                    Thread.currentThread().sleep(controller.getPeriod());
                } catch (InterruptedException ex) {
                    break;
                }
                Map<Integer, Map<Integer, List<MbItem>>> reguest = controller.getVisibleMbItem(true);
                synchronized (reguest) {
                    reguest.forEach((func, addrs) -> {
                        switch (func) {
                            case 3:
                                addrs.forEach((Integer addr, List<MbItem> reg) -> {
                                    reg.sort((MbItem a, MbItem b) -> {
                                        return a.getAddr().compareTo(b.getAddr());
                                    });
                                    reg.forEach((r)->{
                                        r.setValue(null);
                                    });
                                    int addrFirst = reg.get(0).getAddr() + MbItem.addrCorrect;
                                    int addrLast = reg.get(reg.size()-1).getAddr();
                                   
                                    for(; addrFirst < addrLast; addrFirst+= 125)
                                    try {
                                        if(addrLast - addrFirst > 125) 
                                            addrLast += 125 - addrLast - addrFirst;
                                        else
                                            addrLast = reg.get(reg.size()-1).getAddr();
                                        int[] readHoldingRegisters = master.readHoldingRegisters(addr, addrFirst,
                                                 addrLast - addrFirst + 2); //групповой запрос
                                        if (readHoldingRegisters.length >= addrLast - addrFirst + 2)     
                                            for (int i = 0; i < reg.size(); ++i) {
                                                int addrI = reg.get(i).getAddr()+ MbItem.addrCorrect;
                                                
                                                switch (reg.get(i).getType()) {

                                                case int32:
                                                case uint32:
                                                case float32:
                                                            
                                                        reg.get( i ).setValue( readHoldingRegisters[addrI - addrFirst ] << 16 | readHoldingRegisters[addrI - addrFirst +1    ] );
                                                   
                                                    break; 
                                                default:
                                                       
                                                        reg.get( i ).setValue( readHoldingRegisters[ addrI - addrFirst ] );
                                                      
                                                }
                                            }
                                      
                                       // controller.refresh();
                                      
                                    } catch (ModbusProtocolException | ModbusNumberException | ModbusIOException ex) {

                                        Logger.getLogger(Modbus.class.getName()).log(Level.INFO, ex.getMessage());
                                    }// catch (InterruptedException ex) {
                            //  return;
                           // }

                                    reg.forEach((mbItem) -> {

                                        mbItem.getAddr();
                                    });
                                });

                                break;
                        }
                    });
                }
            }
        }
    };
     Modbus(FXMLController controller){  
        this.controller = controller; 
        SerialUtils.setSerialPortFactoryJSSC();
        thread = new Thread(runnable  ,"thread-Modbus" ); 
        com.intelligt.modbus.jlibmodbus.Modbus.setLogLevel(com.intelligt.modbus.jlibmodbus.Modbus.LogLevel.LEVEL_DEBUG);    
        
      

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
        try {
            getModbusMaster().connect();
            if ( this.thread.isAlive()) return true;
            this.thread = new Thread(this.runnable, "thread-Modbus");
            this.thread.setDaemon(true);
            this.thread.start();
        } catch (ModbusIOException ex) {
            Logger.getLogger(Modbus.class.getName()).log(Level.SEVERE, ex.getMessage());
            return false;
        }
        return true;
    }
    
    public void stopReguisition()  {
        try {
            this.thread.interrupt();
            if (this.master != null)
                this.master.disconnect();
        } catch (ModbusIOException ex) {
            Logger.getLogger(Modbus.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Modbus.class.getName()).log(Level.INFO, ex.getMessage());
        }

        return null;
    }
    public void writeReg(){
        
    }
            
}
