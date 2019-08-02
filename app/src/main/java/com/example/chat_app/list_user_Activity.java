package com.example.chat_app;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chat_app.Adabter.listuseradapter;
import com.example.chat_app.Common.Common;
import com.example.chat_app.Holder.QBusersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class list_user_Activity extends AppCompatActivity {

    ListView lstuser;
    Button btn_createchat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_);

        RetrieveAlluser();
        lstuser = (ListView) findViewById(R.id.lst_users);
        lstuser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        btn_createchat = (Button) findViewById(R.id.btn_createchat);
        btn_createchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countchoice = lstuser.getCount();

                if (lstuser.getCheckedItemPositions().size() == 1)

                    createPrivateChat(lstuser.getCheckedItemPositions());

                else if (lstuser.getCheckedItemPositions().size() > 1)

                    createGroupChat(lstuser.getCheckedItemPositions());

                else
                    Toast.makeText(list_user_Activity.this, "please select friend to chat", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog mdialog = new ProgressDialog(list_user_Activity.this);
        mdialog.setMessage("please waitting....");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

        int count_choice = lstuser.getCount();

        for ( int a = 0; a < count_choice; a++) {

            if (checkedItemPositions.get(a)) {
                QBUser user = (QBUser) lstuser.getItemAtPosition(a);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());
                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                        mdialog.dismiss();
                        Toast.makeText(getBaseContext(), "create private chat Dialog Successfully", Toast.LENGTH_LONG).show();
                        finish();

                    }

                    @Override
                    public void onError(QBResponseException e) {

                        Log.e("error", e.getMessage());

                    }
                });
            }


        }
    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(list_user_Activity.this);
        mDialog.setMessage("please waitting....");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        int count_Choice = lstuser.getCount();
        ArrayList<Integer> occupantlist = new ArrayList<>();
        for (int i = 0; i < count_Choice; i++) {

            if (checkedItemPositions.get(i)) {
                QBUser user = (QBUser) lstuser.getItemAtPosition(i);
                occupantlist.add(user.getId());
            }

        }
        //create chat dialog
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createchatDialogname(occupantlist));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantlist);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                mDialog.dismiss();
                Toast.makeText(getBaseContext(), "create chat Dialog Successfully", Toast.LENGTH_LONG).show();
                finish();

            }

            @Override
            public void onError(QBResponseException e) {

                Log.e("error", e.getMessage());

            }
        });

    }

    private void RetrieveAlluser() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBusersHolder.getInstance().putUsers(qbUsers);
                ArrayList<QBUser> qbuserwithoutcurrent = new ArrayList<QBUser>();
                for (QBUser user : qbUsers) {
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                        qbuserwithoutcurrent.add(user);
                }

                listuseradapter adapter = new listuseradapter(getBaseContext(), qbuserwithoutcurrent);
                lstuser.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("error", e.getMessage());
            }
        });
    }
}
