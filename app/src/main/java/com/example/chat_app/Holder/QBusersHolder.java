package com.example.chat_app.Holder;

//for cache

import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class QBusersHolder {

    private static QBusersHolder instance;
    private SparseArray<QBUser> qbUserSparseArray;
    public static synchronized QBusersHolder getInstance(){
        if(instance == null)
            instance = new QBusersHolder();
                    return instance;
    }

    private QBusersHolder(){
        qbUserSparseArray = new SparseArray<>();
    }

    public void putUsers(List<QBUser> users){

        for (QBUser user : users)
            putUser(user);

    }

    public void putUser(QBUser user){
       qbUserSparseArray.put(user.getId() , user);
    }

    public QBUser getuerbyid(int id){
        return qbUserSparseArray.get(id);
    }

    public List<QBUser> getusersbyids(List<Integer> ids){

        List<QBUser> qbUser = new ArrayList<>();
        for (Integer id : ids){
            QBUser user = getuerbyid(id);
            if(user != null)
                qbUser.add(user);
        }
        return qbUser;

    }

}
