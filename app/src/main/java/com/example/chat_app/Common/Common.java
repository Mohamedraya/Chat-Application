package com.example.chat_app.Common;

import com.example.chat_app.Holder.QBusersHolder;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class Common {

    public static final String Dialog_Extra = "Dialog";

    //write method to create name of chat dialog from user list

    public static String createchatDialogname (List<Integer> qbusers){

        List<QBUser> qbUsers1 = QBusersHolder.getInstance().getusersbyids(qbusers);
        StringBuilder name = new StringBuilder();

        for (QBUser user : qbUsers1)
            name.append(user.getFullName()).append(" ");

        if (name.length() > 30)
            name = name.replace( 30 , name.length()-1 , "...");


         return name.toString();
    }

    public static Boolean isnulloremptystring (String content){

        return (content != null && !content.trim().isEmpty()?false:true);
    }
}
