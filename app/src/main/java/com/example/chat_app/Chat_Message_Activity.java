package com.example.chat_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chat_app.Adabter.ChatMessageAdapter;
import com.example.chat_app.Common.Common;
import com.example.chat_app.Holder.QBChatMessagesHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;

public class Chat_Message_Activity extends AppCompatActivity {

    QBChatDialog qbChatDialog;
    ListView lstchatMessage;
    ImageButton submitButton;
    EditText edtContent;
    ChatMessageAdapter adapter;

    // variable for edit/delete message
    int contextMenuIndexClicked = -1;
    Boolean iseditmode = false;
    QBChatMessage editMessage;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_message_context_menu , menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        contextMenuIndexClicked = info.position;
        switch (item.getItemId()){
            case R.id.chat_message_update_message:
                updateMessage();
                break;
            case R.id.chat_message_delete_message:
                deleteMessage();
                break;
                default:
                    break;
        }
        return true;
    }

    private void deleteMessage() {
        editMessage = QBChatMessagesHolder.getInstance().getchatMessagebyid(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);
        QBRestChatService.deleteMessage(editMessage.getId(),false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                RetrieveAllMessage();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    private void updateMessage() {
        // set message for edt text
        editMessage = QBChatMessagesHolder.getInstance().getchatMessagebyid(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);
        edtContent.setText(editMessage.getBody());
        iseditmode = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__message_);
        
        initView();

        initcahtDialog();

        RetrieveAllMessage();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!iseditmode) {
                    QBChatMessage chatMessage = new QBChatMessage();
                    chatMessage.setBody(edtContent.getText().toString());
                    chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                    chatMessage.setSaveToHistory(true);
                    try {
                        qbChatDialog.sendMessage(chatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }

                    //put message to cache
                    QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), chatMessage);
                    ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getchatMessagebyid(qbChatDialog.getDialogId());
                    adapter = new ChatMessageAdapter(getBaseContext(), messages);
                    lstchatMessage.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    // remove text from edtText
                    edtContent.setText("");
                    edtContent.setFocusable(true);
                }
                else {
                    QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                    messageUpdateBuilder.updateText(edtContent.getText().toString()).markDelivered().markRead();
                    QBRestChatService.updateMessage(editMessage.getId(),qbChatDialog.getDialogId(),messageUpdateBuilder)
                            .performAsync(new QBEntityCallback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid, Bundle bundle) {
                                    // refresh data
                                    RetrieveAllMessage();
                                    iseditmode = false;
                                    edtContent.setText("");
                                    edtContent.setFocusable(true);
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    Toast.makeText(Chat_Message_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }

            }
        });

    }

    private void RetrieveAllMessage() {

        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);
        if (qbChatDialog != null){

            QBRestChatService.getDialogMessages(qbChatDialog , messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                    // put message to cache
                    QBChatMessagesHolder.getInstance().putMessages(qbChatDialog.getDialogId() , qbChatMessages);
                    adapter = new ChatMessageAdapter(getBaseContext() , qbChatMessages);
                    lstchatMessage.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });

        }

    }

    private void initcahtDialog() {

        qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.Dialog_Extra);
        qbChatDialog.initForChat(QBChatService.getInstance());

        // Register listener incoming message
        QBIncomingMessagesManager incomingMessages = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessages.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });

        qbChatDialog.addMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                // save message to cache and refresh listview
                QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(), qbChatMessage);
                ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getchatMessagebyid(qbChatMessage.getDialogId());
                adapter = new ChatMessageAdapter(getBaseContext() , messages);
                lstchatMessage.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

                Log.e("error" , e.getMessage());
            }
        });

    }

    private void initView() {

        lstchatMessage = (ListView)findViewById(R.id.list_of_message);
        submitButton = (ImageButton)findViewById(R.id.send_Button);
        edtContent = (EditText)findViewById(R.id.edt_content);

        // Add context menu
        registerForContextMenu(lstchatMessage);

    }
}
