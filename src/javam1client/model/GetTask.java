/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author Mauyz
 */

public class GetTask extends Task<ObservableList<Client>> {
    
    JSONArray list ;
    
    public GetTask(JSONArray list) {
        this.list = list;
    }
    @Override@SuppressWarnings("SleepWhileInLoop")
        protected ObservableList<Client> call() throws Exception {
            for (int i = 0; i < 400; i++) {
                updateProgress(i,400);
                Thread.sleep(3);
            }
            ObservableList<Client> listClient = FXCollections.observableArrayList();
            for(Object obj : list){
                JSONObject client = JSONObject.fromObject(obj);
                Integer num = client.getInt("numero");
                String nom = client.getString("nom");
                String adr = client.getString("adresse");
                Integer solde = client.getInt("solde");
                listClient.add(new Client(num, nom, adr, solde));
            }            
            return listClient;
        }
    }
