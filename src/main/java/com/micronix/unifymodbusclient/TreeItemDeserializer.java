/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import java.lang.reflect.Type;
import java.util.Map;
import javafx.scene.control.TreeItem;

/**
 *
 * @author alekseev
 */
public class TreeItemDeserializer implements JsonDeserializer<TreeItem<MbItem>> {

    @Override
    public TreeItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
       TreeItem treeItem = new TreeItem();
       if(json.getAsJsonObject().has("addr"))
           MbItem.addrCorrect = json.getAsJsonObject().get("addr").getAsInt();
       if (json.getAsJsonObject().has("BaudRate")){
           FXMLController.getSerialParameters().setBaudRate(SerialPort.BaudRate.getBaudRate(json.getAsJsonObject().get("BaudRate").getAsInt()));
           FXMLController.getSerialParameters().setDataBits(json.getAsJsonObject().get("DataBits").getAsInt());
           FXMLController.getSerialParameters().setParity(SerialPort.Parity.valueOf(json.getAsJsonObject().get("Parity").getAsString()));
           FXMLController.getSerialParameters().setStopBits(json.getAsJsonObject().get("StopBits").getAsInt());
       }
       exec(json.getAsJsonObject() ,treeItem);
        return treeItem;

    }
    Type itemsMapType = new TypeToken<Map<Integer, String>>() {}.getType();
    private void exec(JsonObject jsonObject, TreeItem treeItem)
    {
     
        if (jsonObject.has( "getChildren()" ))           
         jsonObject.get("getChildren()").getAsJsonArray().forEach((JsonElement item)->{
             MbItem  mbItem;
             if (item.getAsJsonObject().has("type")){
                 mbItem =   new MbItem( item.getAsJsonObject().get("name").getAsString(),
                                ( (MbItem)(treeItem.getValue()) ).getServer(),
                                MbItem.Access.valueOf(item.getAsJsonObject().get("access").getAsString()),
                                item.getAsJsonObject().get("addr").getAsInt(),
                                MbItem.Type.valueOf(item.getAsJsonObject().get("type").getAsString()),
                                MbItem.Func.valueOf(item.getAsJsonObject().get("func").getAsString()),
                                item.getAsJsonObject().has("defaultValue")
                                        ? item.getAsJsonObject().get("defaultValue").getAsInt():null,
                                item.getAsJsonObject().has("min")
                                         ?item.getAsJsonObject().get("min").getAsFloat():null,
                                item.getAsJsonObject().has("max")
                                         ?item.getAsJsonObject().get("max").getAsFloat():null
                 );
                 if (item.getAsJsonObject().has("map"))
                    mbItem.setMap(new Gson().fromJson(item.getAsJsonObject().get("map"), itemsMapType));
            }else
              mbItem =   new MbItem(item.getAsJsonObject().get("name").getAsString(),
                                    item.getAsJsonObject().get("addr").getAsInt(),
                                    item.getAsJsonObject().has("func")
                                            ?MbItem.Func.valueOf( item.getAsJsonObject().get("func").getAsString()) :null); 
             TreeItem newTreeItem =  mbItem.getTreeItem() ;
             newTreeItem.setExpanded(true);
             treeItem.getChildren().add(newTreeItem);
             exec(item.getAsJsonObject(), newTreeItem);             
         });

    } 
           
}
