package com.example.chat_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat_app.Common.Common;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class UserProfile_Activity extends AppCompatActivity {

    EditText edt_oldpassword , edt_newpassword , edt_fullname , edt_email , edt_phone;
    Button btn_update , btn_cancel;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_update_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.user_update_logout:
                logout();
                break;
                default:
                    break;
        }
        return true;
    }

    private void logout() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        Toast.makeText(UserProfile_Activity.this, "you are logout", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfile_Activity.this , MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//remove all previous activity
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_);

        Toolbar toolbar = (Toolbar)findViewById(R.id.user_update_toolbar);
        toolbar.setTitle("Chat App");
        setSupportActionBar(toolbar);

        initViews();
        loaduserprofile();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpassword = edt_oldpassword.getText().toString();
                String newpassword = edt_newpassword.getText().toString();
                String fullname    = edt_fullname.getText().toString();
                String email       = edt_email.getText().toString();
                String phone       = edt_phone.getText().toString();

                QBUser user = new QBUser();
                user.setId(QBChatService.getInstance().getUser().getId());
                if (Common.isnulloremptystring(oldpassword));
                user.setOldPassword(oldpassword);

                if (Common.isnulloremptystring(newpassword));
                user.setPassword(newpassword);

                if (Common.isnulloremptystring(fullname));
                user.setFullName(fullname);

                if (Common.isnulloremptystring(email));
                user.setEmail(email);

                if (Common.isnulloremptystring(phone));
                user.setPhone(phone);
                final ProgressDialog mdialog = new ProgressDialog(UserProfile_Activity.this);
                mdialog.setMessage("Please Wait...");
                mdialog.show();

                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle bundle) {

                        Toast.makeText(UserProfile_Activity.this, "User: "+user.getLogin()+"updated", Toast.LENGTH_LONG).show();
                        mdialog.dismiss();

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(UserProfile_Activity.this, "error" +e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void loaduserprofile() {
        QBUser currentuser = QBChatService.getInstance().getUser();
        String fullname    = currentuser.getFullName();
        String email = currentuser.getEmail();
        String phone = currentuser.getPhone();

        edt_fullname.setText(fullname);
        edt_email.setText(email);
        edt_phone.setText(phone);
    }

    private void initViews() {
        btn_update = (Button)findViewById(R.id.update_user_btn_update);
        btn_cancel = (Button)findViewById(R.id.update_user_btn_cancel);
        edt_oldpassword = (EditText)findViewById(R.id.update_edt_old_password);
        edt_newpassword = (EditText)findViewById(R.id.update_edt_password);
        edt_fullname = (EditText)findViewById(R.id.update_edt_fullname);
        edt_email = (EditText)findViewById(R.id.update_edt_email);
        edt_phone = (EditText)findViewById(R.id.update_edt_phone);
    }
}
