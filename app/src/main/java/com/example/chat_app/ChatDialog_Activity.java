package com.example.chat_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.chat_app.Adabter.ChatDialogAdabter;
import com.example.chat_app.Common.Common;
import com.example.chat_app.Holder.QBunreadMssageHolder;
import com.example.chat_app.Holder.QBusersHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatDialog_Activity extends AppCompatActivity implements QBChatDialogMessageListener , QBSystemMessageListener{

    FloatingActionButton actionButton;
    ListView lstchat;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_dialog_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.chat_dialog_menu_user:
            showuserprofile();
            break;
            default:
                break;
        }
        return true;
    }

    private void showuserprofile() {

        Intent intent = new Intent(ChatDialog_Activity.this , UserProfile_Activity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadchatDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog_);

        Toolbar toolbar = (Toolbar)findViewById(R.id.chat_dialog_toolbar);
        toolbar.setTitle("Chat app");
        setSupportActionBar(toolbar);

        createSessionchat();

        lstchat = (ListView)findViewById(R.id.lst_chatDialog);
        lstchat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBChatDialog qbChatDialog = (QBChatDialog)lstchat.getAdapter().getItem(position);
                Intent intent = new Intent(ChatDialog_Activity.this , Chat_Message_Activity.class);
                intent.putExtra(Common.Dialog_Extra , qbChatDialog);
                startActivity(intent);
            }
        });

        loadchatDialog();

        actionButton = (FloatingActionButton)findViewById(R.id.chatDialog_adduser);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDialog_Activity.this , list_user_Activity.class);
                startActivity(intent);
            }
        });

    }



    private void createSessionchat() {
        final ProgressDialog mDialog = new ProgressDialog(ChatDialog_Activity.this);
        mDialog.setMessage("Please Waitting.....");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();



        //load all user and save to cache

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBusersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
        String user , password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");

        final QBUser qbUser = new QBUser(user , password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }
                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                       mDialog.dismiss();
                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener((QBSystemMessageListener) ChatDialog_Activity.this);
                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(ChatDialog_Activity.this);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("error" , ""+e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void loadchatDialog() {
        QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
        requestGetBuilder.setLimit(100);
        QBRestChatService.getChatDialogs(null , requestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {

                // unread setting
                Set<String> setid = new HashSet<>();
                for (QBChatDialog chatDialog : qbChatDialogs )
                    setid.add(chatDialog.getDialogId());

                // get message unread
                QBRestChatService.getTotalUnreadMessagesCount(setid , QBunreadMssageHolder.getInstance().getBundle()).performAsync(new QBEntityCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer, Bundle bundle) {
                        // save to cache
                        QBunreadMssageHolder.getInstance().setbundle(bundle);
                        // refresh list dialog
                        ChatDialogAdabter adabter = new ChatDialogAdabter(getBaseContext() , qbChatDialogs);
                        lstchat.setAdapter(adabter);
                        adabter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {
               Log.e("error" , e.getMessage());
            }
        });
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

        loadchatDialog();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processMessage(QBChatMessage qbChatMessage) {

    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {

    }
}
