/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;

/**
 *
 * @author alekseev
 */
public class MbItem {
    
    private String name;
    private String access;
    private Integer addr;
    private String type; 
    private Integer point;
    private ProgressBar bar;
    private Map<Integer,String>  map;
    public MbItem(String name, String access, Integer addr, String type)
    {
        this.name = name;
        this.access =access;
        this.addr = addr;
        this.type = type;
        this.map =  new HashMap();
        bar = new ProgressBar(0.5);
        
    }
    public MbItem( String name )
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
        this.access = access;    
    }
    public Integer getAddr(){
        return addr;
    }
    
    public void setAddr(Integer addr ){
        this.addr = addr;    
    }
    public String getType(){
        return type;
    }
    
    public void setType(String type){
        this.type = type;    
    }
    public Integer getPoint(){
        return point;
    }
    
    public void setPoint(Integer point){
        this.point = point;    
    }
        public ProgressBar getBar(){
        return bar;
    }
    
    public void setBar(ProgressBar bar){
        this.bar = bar;    
    }
    public void setMap(Integer point){
        this.point = point;    
    }
        public Map getMap(){
        return map;
    }
    
    public void setMap(HashMap map){
        this.map = map;    
    }
    
}
