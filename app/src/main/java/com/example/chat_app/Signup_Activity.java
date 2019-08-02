package com.example.chat_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.model.QBEntity;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class Signup_Activity extends AppCompatActivity {

    Button btnSignup , btnCancel;
    EditText txtuser , txtfullname ,txtpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_);

        Registersession();

        btnSignup = (Button)findViewById(R.id.signup_btnsignup);
        btnCancel = (Button)findViewById(R.id.signup_btncancel);
        txtuser = (EditText)findViewById(R.id.signup_edit_login);
        txtfullname = (EditText)findViewById(R.id.signup_txtfullname);
        txtpassword = (EditText)findViewById(R.id.signup_edit_password);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = txtuser.getText().toString();
                String password = txtpassword.getText().toString();
                QBUser qbUser = new QBUser(user , password);
                qbUser.setFullName(txtfullname.getText().toString());
                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext() , "Sign up Successfully" , Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext() , ""+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void Registersession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

                Log.e("Error" , e.getMessage());
            }
        });
    }
}
