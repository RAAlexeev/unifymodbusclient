/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

/**
 *
 * @author alekseev
 */
public class MbItem {
    
    private String name;
    private String access;
    private int addr;
    private String type; 
    private int point;
    private ProgressBar bar;
 
    public MbItem(String name, String access, int addr, String type)
    {
        this.name = name;
        this.access =access;
        this.addr = addr;
        this.type = type;
        bar = new ProgressBar(0.5);
    }
    public MbItem(String name)
    {
        this.name = name; 
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;    
    }
    
    public String getAccess(){
        return access;
    }
    
    public void setAccess(String access){
        this.name = access;    
    }
    public int getAddr(){
        return addr;
    }
    
    public void setAddr(Byte addr ){
        this.addr = addr;    
    }
    public String getType(){
        return type;
    }
    
    public void setType(String Type){
        this.type = type;    
    }
    public int getPoint(){
        return point;
    }
    
    public void setPoint(int point){
        this.point = point;    
    }
        public ProgressBar getBar(){
        return bar;
    }
    
    public void setBar(ProgressBar bar){
        this.bar = bar;    
    }
    
    
}
