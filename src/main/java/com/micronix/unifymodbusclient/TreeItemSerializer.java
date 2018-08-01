/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import javafx.scene.control.TreeItem;

/**
 *
 * @author alekseev
 */
public class TreeItemSerializer implements JsonSerializer<TreeItem<MbItem>>{

    @Override
    public JsonElement serialize(TreeItem<MbItem> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
       jsonObject.addProperty( "addr", MbItem.addrCorrect );
        jsonObject.addProperty( "BaudRate", FXMLController.getSerialParameters().getBaudRate() );
        jsonObject.addProperty( "DataBits", FXMLController.getSerialParameters().getDataBits() );
        jsonObject.addProperty( "Parity", FXMLController.getSerialParameters().getParity().name() );
        jsonObject.addProperty( "StopBits", FXMLController.getSerialParameters().getStopBits() );
        exec(src, jsonObject );   
        return jsonObject;
    }
   
    java.lang.reflect.Type itemsMapType = new TypeToken<Map<Integer, String>>() {}.getType();

     public void exec(TreeItem treeItem, JsonObject jsonObject)
    {
           
           if ( treeItem.getValue() != null ){
              MbItem  mbItem = (MbItem) treeItem.getValue();
              jsonObject.addProperty("addr", mbItem.getAddr());
              jsonObject.addProperty("access", mbItem.getAccess().name());
              jsonObject.addProperty("name", mbItem.getName());
              jsonObject.addProperty("point", mbItem.getPoint());
              if ( mbItem.getType() != null )
              jsonObject.addProperty("type", mbItem.getType().name());
              if ( mbItem.getFunc() != null )
              jsonObject.addProperty("func", mbItem.getFunc().name());
              if( mbItem.getBarOrDefaultValue() instanceof Integer  ) 
                jsonObject.addProperty("defaultValue", (Integer)mbItem.getBarOrDefaultValue());
              jsonObject.add("map", new Gson().toJsonTree(mbItem.getMap(), itemsMapType));
              if(mbItem.getMin() != null)
                 jsonObject.addProperty("min", mbItem.getMin());
              if(mbItem.getMax() != null)
                 jsonObject.addProperty("max", mbItem.getMax());
           }
           
           JsonArray child = new JsonArray();
           jsonObject.add("getChildren()", child);
          treeItem.getChildren().forEach( item->{
              JsonObject newJsonObject = new JsonObject();
              child.add(newJsonObject);
              exec((TreeItem) item,newJsonObject);     
           });
         
    }
    
}
