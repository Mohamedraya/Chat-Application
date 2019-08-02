package com.example.chat_app.Holder;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QBChatMessagesHolder {

    private static QBChatMessagesHolder instance;
    private HashMap<String , ArrayList<QBChatMessage>> qbchatmessageArray;

    public static synchronized QBChatMessagesHolder getInstance(){

        QBChatMessagesHolder qbChatMessagesHolder;
        synchronized(QBChatMessagesHolder.class){
            if (instance == null)
                instance = new  QBChatMessagesHolder();
            qbChatMessagesHolder = instance;
        }

        return qbChatMessagesHolder;

    }

    private QBChatMessagesHolder(){

      this.qbchatmessageArray = new HashMap<>();
    }

    public void putMessages (String dialogid , ArrayList<QBChatMessage> qbChatMessages){

        this.qbchatmessageArray.put( dialogid , qbChatMessages);

    }

    public void putMessage (String dialogid , QBChatMessage qbChatMessage){

        List<QBChatMessage> lstResult = (List)qbchatmessageArray.get(dialogid);
        lstResult.add(qbChatMessage);
        ArrayList<QBChatMessage> lstadded = new ArrayList<>(lstResult.size());
        lstadded.addAll(lstResult);
        putMessages(dialogid , lstadded);
    }

    public ArrayList<QBChatMessage> getchatMessagebyid (String dialogid){

        return (ArrayList<QBChatMessage>)this.qbchatmessageArray.get(dialogid);

    }



}
