package projects.android.acupuncturepoint.Views.Login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.zing.zalo.zalosdk.oauth.LoginForm;
import com.zing.zalo.zalosdk.oauth.LoginVia;
import com.zing.zalo.zalosdk.oauth.OAuthCompleteListener;
import com.zing.zalo.zalosdk.oauth.OauthResponse;
import com.zing.zalo.zalosdk.oauth.ValidateOAuthCodeCallback;
import com.zing.zalo.zalosdk.oauth.ZaloSDK;

import java.util.Date;

import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.MainView.MainActivity;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    CallbackManager callbackManager;
    LoginButton loginButton;
    LoginForm loginForm;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;
    private static final int RC_SIGN_IN = 007;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        btnSignIn.setOnClickListener(view -> signIn());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e("", "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            Log.e("", "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);
            startActivity(new Intent(this, MainActivity.class));

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        ZaloSDK.Instance.onActivityResult(this, requestCode, resultCode, data);
        if (loginForm != null) {
            loginForm.onActivityResult(this, requestCode, resultCode, data);
        }
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private class LoginListener extends OAuthCompleteListener {

        private Activity ctx;

        public LoginListener(Activity ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onSkipProtectAcc(Dialog dialog) {
            dialog.dismiss();
        }

        @Override
        public void onProtectAccComplete(int errorCode, String message, Dialog dialog) {

            if (errorCode == 0) {

                ZaloSDK.Instance.isAuthenticate(new ValidateOAuthCodeCallback() {

                    @Override
                    public void onValidateComplete(boolean validated, int error_Code, long userId, String oauthCode) {

                    }
                });

                dialog.dismiss();
            }

            com.zing.zalo.zalosdk.payment.direct.Utils.showAlertDialog(ctx, message, null);

        }

        @Override
        public void onAuthenError(int errorCode, String message) {
            if (ctx != null && !TextUtils.isEmpty(message))
                com.zing.zalo.zalosdk.payment.direct.Utils.showAlertDialog(ctx, message, null);
            super.onAuthenError(errorCode, message);
        }

        @Override
        public void onGetOAuthComplete(OauthResponse response) {
            super.onGetOAuthComplete(response);
        }
    }
}
