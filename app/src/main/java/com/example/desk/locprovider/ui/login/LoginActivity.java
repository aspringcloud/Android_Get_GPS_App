package com.example.desk.locprovider.ui.login;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.desk.locprovider.MainActivity;
import com.example.desk.locprovider.R;
import com.example.desk.locprovider.SaveSharedPreference;
import com.example.desk.locprovider.ui.login.LoginViewModel;
import com.example.desk.locprovider.ui.login.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = usernameEditText.getText().toString();
                String pw = passwordEditText.getText().toString();
                if ((id.equalsIgnoreCase( "1146") && pw.equalsIgnoreCase("1146"))
                        || (id.equalsIgnoreCase( "1147") && pw.equalsIgnoreCase("1147"))
                        || (id.equalsIgnoreCase( "6894") && pw.equalsIgnoreCase("6894"))
                        || (id.equalsIgnoreCase( "6895") && pw.equalsIgnoreCase("6895"))) {
                    Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    SaveSharedPreference.setCarNumber(LoginActivity.this, usernameEditText.getText().toString());
                    startActivity(MainIntent);
                    Toast.makeText(LoginActivity.this,"로그인 성공",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(LoginActivity.this,"로그인 실페",Toast.LENGTH_LONG).show();
                }
     //           loginViewModel.login(usernameEditText.getText().toString(),
       //                 passwordEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);

        String id = usernameEditText.getText().toString();
        String pw = passwordEditText.getText().toString();
        if ((id.equalsIgnoreCase( "1146") && pw.equalsIgnoreCase("1146"))
                || (id.equalsIgnoreCase( "1147") && pw.equalsIgnoreCase("1147"))
                || (id.equalsIgnoreCase( "6894") && pw.equalsIgnoreCase("6894"))
                || (id.equalsIgnoreCase( "6895") && pw.equalsIgnoreCase("6895"))) {
            Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
            SaveSharedPreference.setCarNumber(LoginActivity.this, usernameEditText.getText().toString());
            startActivity(MainIntent);
            Toast.makeText(LoginActivity.this,"로그인 성공",Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(LoginActivity.this,"로그인 실페",Toast.LENGTH_LONG).show();
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}