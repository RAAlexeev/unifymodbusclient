/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.micronix.unifymodbusclient;

import java.util.ArrayList;
import javafx.scene.control.TreeItem;

/**
 *
 * @author alekseev
 */
public class TreeItemDelRestore {
  static private final  ArrayList deletedItems = new ArrayList(); 
  static private class SaveItem{  
       TreeItem item, perent;
        int indexOf;
       SaveItem(TreeItem item,int indexOf){
          this.item = item;
          this.perent =item.getParent();
          this.indexOf = indexOf;
       }

   }
    TreeItemDelRestore(TreeItem item){
       saveAndDelete(item); 
    }
static public final void saveAndDelete(TreeItem item){ 
      if(item.getParent()==null)  return;
     deletedItems.add(new SaveItem(item, item.getParent().getChildren().indexOf(item)));
     item.getParent().getChildren().remove(item);
  }
 static public final TreeItem restore(){
      if( deletedItems.isEmpty() ) return null;
      SaveItem rItem = (SaveItem)(deletedItems.get(deletedItems.size()-1));
          rItem.perent.getChildren().add(rItem.indexOf, rItem.item);
          deletedItems.remove(rItem);
      return rItem.item;
  }

}
