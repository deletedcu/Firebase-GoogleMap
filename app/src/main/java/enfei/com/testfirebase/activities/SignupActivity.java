package enfei.com.testfirebase.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import enfei.com.testfirebase.R;
import enfei.com.testfirebase.services.FirebaseService;
import enfei.com.testfirebase.services.ObjectResultListener;

/**
 * Created by king on 17/08/2017.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText textEmail;
    private EditText textPassword;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        textEmail = (EditText) findViewById(R.id.email);
        textPassword = (EditText) findViewById(R.id.password);
        Button btnSignup = (Button) findViewById(R.id.btn_signup);
        Button btnSignin = (Button) findViewById(R.id.btn_signin);
        btnSignup.setOnClickListener(this);
        btnSignin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                hideSoftKeyboard();
                signup();
                break;
            case R.id.btn_signin:
                hideSoftKeyboard();
                finish();
                break;
        }
    }

    @Override
    public void onPause() {
        hideProgressDialog();
        super.onPause();
    }

    private void signup() {
        final String email = textEmail.getText().toString().trim();
        String password = textEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), getString(R.string.minimum_password), Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("");

        FirebaseService.shared.signup(email, password, new ObjectResultListener() {
            @Override
            public void onResult(boolean isSuccess, String error, Object object) {

                if (!isSuccess) {
                    hideProgressDialog();
                    Toast.makeText(SignupActivity.this, "Authentication failed." + error,
                            Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseService.shared.sendVerificationEmail(new ObjectResultListener() {
                        @Override
                        public void onResult(boolean isSuccess, String error, Object object) {
                            hideProgressDialog();
                            if (isSuccess)
                                Toast.makeText(SignupActivity.this, "Email Verification Sent. Please check your email and follow the provided link for verification", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(SignupActivity.this, error, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }
        });

    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showProgressDialog(String title) {
        progressDialog = ProgressDialog.show(this, title, "");
    }

    private void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = null;
    }

}
