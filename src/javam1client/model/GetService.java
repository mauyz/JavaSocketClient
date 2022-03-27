/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1client.model;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.sf.json.JSONArray;

/**
 *
 * @author Mauyz
 */

public class GetService extends Service<ObservableList<Client>> {
    
    private JSONArray array = null;
        
    public GetService(JSONArray list){
        array = list;
    }
        
    @Override
    protected Task createTask() {
        return new GetTask(array);
    }

    public void setArray(JSONArray array) {
        this.array = array;
    }
        
}
