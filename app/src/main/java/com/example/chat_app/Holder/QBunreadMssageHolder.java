package com.example.chat_app.Holder;

import android.os.Bundle;

public class QBunreadMssageHolder {

    private static QBunreadMssageHolder instance;
    private Bundle bundle;

    public static synchronized QBunreadMssageHolder getInstance(){

        QBunreadMssageHolder qBunreadMssageHolder;
        synchronized(QBunreadMssageHolder.class){
            if (instance == null)
                instance = new QBunreadMssageHolder();
            qBunreadMssageHolder = instance;

        }
        return qBunreadMssageHolder;
    }

    private QBunreadMssageHolder(){
        bundle = new Bundle();
    }

    public void setbundle(Bundle bundle){
        this.bundle = bundle;

    }

    public Bundle getBundle(){
        return this.bundle;
    }

    public int getunreadmessageBydialogid(String id){

        return this.bundle.getInt(id);

    }


}
