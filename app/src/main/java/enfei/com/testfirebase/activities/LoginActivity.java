package enfei.com.testfirebase.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import enfei.com.testfirebase.Constants;
import enfei.com.testfirebase.MyApplication;
import enfei.com.testfirebase.R;
import enfei.com.testfirebase.models.CurrentUser;
import enfei.com.testfirebase.models.User;
import enfei.com.testfirebase.services.FirebaseService;
import enfei.com.testfirebase.services.ObjectResultListener;

/**
 * Created by king on 17/08/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FacebookLogin";
    private EditText textEmail;
    private EditText textPassword;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;

    //Facebook CallbackManager
    CallbackManager callbackManager;

    private User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        if (FirebaseService.shared.isLoggedIn()) {
            showProgressDialog("");
            FirebaseService.shared.getUser(auth.getCurrentUser().getUid(), new ObjectResultListener() {
                @Override
                public void onResult(boolean isSuccess, String error, Object object) {
                    hideProgressDialog();
                    if (isSuccess) {
                        CurrentUser.login((User) object);
                        startMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        textEmail = (EditText) findViewById(R.id.email);
        textPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnFacebook = (Button) findViewById(R.id.btn_facebook);
        Button btnSignup = (Button) findViewById(R.id.btn_signup);
        Button btnForgot = (Button) findViewById(R.id.btn_forgot);

        btnLogin.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnForgot.setOnClickListener(this);

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v(TAG, response.toString());

                                try {
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String gender = object.getString("gender");
                                    String birthday = object.getString("birthday");
                                    mUser = new User(name, email, gender, birthday, Constants.TYPE_FACEBOOK);
                                    MyApplication.mImageLoader.loadImage(String.format("http://graph.facebook.com/%s/picture?type=square", id), new ImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String s, View view) {

                                        }

                                        @Override
                                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                                            handleFacebookAccessToken(loginResult.getAccessToken());
                                        }

                                        @Override
                                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                            mUser.image = bitmap;
                                            handleFacebookAccessToken(loginResult.getAccessToken());
                                        }

                                        @Override
                                        public void onLoadingCancelled(String s, View view) {

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onPause() {
        hideProgressDialog();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                hideSoftKeyboard();
                login();
                break;
            case R.id.btn_facebook:
                loginWithFacebook();
                break;
            case R.id.btn_forgot:
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                break;
            case R.id.btn_signup:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                break;
        }
    }

    private void login() {

        String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 6) {
            textPassword.setError(getString(R.string.minimum_password));
            return;
        }

        showProgressDialog("");
        FirebaseService.shared.login(email, password, new ObjectResultListener() {
            @Override
            public void onResult(boolean isSuccess, String error, Object object) {
                hideProgressDialog();
                if (!isSuccess) {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                } else {
                    CurrentUser.login((User) object);
                    startMainActivity();
                }
            }
        });
    }

    private void loginWithFacebook() {
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "user_friends", "email", "user_birthday")
                );
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog("");
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseService.shared.signinWithFacebook(credential, mUser, new ObjectResultListener() {
            @Override
            public void onResult(boolean isSuccess, String error, Object object) {
                hideProgressDialog();
                if (isSuccess) {
                    Log.d(TAG, "signInWithCredential:success");
                    startMainActivity();
                } else {
                    Log.d(TAG, "signInWithCredential:failure");
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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
