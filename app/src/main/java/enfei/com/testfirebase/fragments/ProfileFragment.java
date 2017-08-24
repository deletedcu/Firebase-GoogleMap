package enfei.com.testfirebase.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import enfei.com.testfirebase.Constants;
import enfei.com.testfirebase.R;
import enfei.com.testfirebase.activities.LoginActivity;
import enfei.com.testfirebase.activities.ResetPasswordActivity;
import enfei.com.testfirebase.models.CurrentUser;
import enfei.com.testfirebase.models.User;

/**
 * Created by king on 17/08/2017.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View view;
    private CircleImageView ivAvatar;
    private TextView tvName;
    private ImageView btnProfile;
    private ImageView btnHistory;
    private ImageView btnPayment;
    private ImageView btnHelp;
    private Button btnResetPassword;
    private Button btnLogout;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(final LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        };

        initView();

        return view;
    }

    private void initView() {

        ivAvatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        btnProfile = (ImageView) view.findViewById(R.id.iv_profile);
        btnHistory = (ImageView) view.findViewById(R.id.iv_history);
        btnPayment = (ImageView) view.findViewById(R.id.iv_payment);
        btnHelp = (ImageView) view.findViewById(R.id.iv_help);
        btnResetPassword = (Button) view.findViewById(R.id.btn_reset_password);
        btnLogout = (Button) view.findViewById(R.id.btn_logout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        btnProfile.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnPayment.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        User user = CurrentUser.shared.user;
        try {
            if (!TextUtils.isEmpty(user.name)) {
                tvName.setText(user.name);
            }
            if (user.image != null) {
                ivAvatar.setImageBitmap(user.image);
            }

            if (user.loginType == Constants.TYPE_FACEBOOK) {
                btnResetPassword.setVisibility(View.GONE);
            } else {
                btnResetPassword.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException e) {

        }



    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile:

                break;
            case R.id.iv_history:

                break;
            case R.id.iv_payment:

                break;
            case R.id.iv_help:

                break;
            case R.id.btn_reset_password:
                resetPassword();
                break;
            case R.id.btn_logout:
                showConfirmDialog();
                break;
        }
    }

    private void resetPassword() {
        Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.btn_log_out);
        builder.setMessage(R.string.msg_log_out);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                logout();
            }
        });
        builder.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void logout() {
        auth.signOut();
        CurrentUser.logout();
    }

}
