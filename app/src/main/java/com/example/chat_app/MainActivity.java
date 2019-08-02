package com.example.chat_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {

    static final String App_id = "77092";
    static final String Auth_key = "NTsG6ACCEcDp36N";
    static final String Auth_secret = "7vaqgnbV9Dq67nF";
    static final String Account_key = "vtp9RvL9GqrhzSenNPez";

    Button btnlogin , btnsignup;
    EditText edtuername , edtpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFramework();

        btnlogin = (Button)findViewById(R.id.main_btnlogin);
        btnsignup =(Button)findViewById(R.id.main_btnsignup);

        edtuername = (EditText)findViewById(R.id.main_edit_login);
        edtpassword = (EditText)findViewById(R.id.main_edit_password);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , Signup_Activity.class));
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = edtuername.getText().toString();
                final String password = edtpassword.getText().toString();
                QBUser qbUser = new QBUser(user , password);
                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext() , "login Successfuly" , Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this , ChatDialog_Activity.class);
                        intent.putExtra("user" , user);
                        intent.putExtra("password" , password);
                        startActivity(intent);
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

    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), App_id , Auth_key , Auth_secret);
        QBSettings.getInstance().setAccountKey(Account_key);
    }
}
