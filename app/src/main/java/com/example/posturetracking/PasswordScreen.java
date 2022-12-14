package com.example.posturetracking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class PasswordScreen extends AppCompatActivity{

    private TextView passwordTextView, errorMessageText;
    private Button button, saveBtn;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_password_screen);
        } else {
            setContentView(R.layout.activity_password_screen_landscape);
        }
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = getSharedPreferences("SettingsStore", Context.MODE_PRIVATE);

        passwordTextView = findViewById(R.id.textView6);
        errorMessageText = findViewById(R.id.error);

        button = findViewById(R.id.button4);
        saveBtn = findViewById(R.id.imageButton);
    }

    public void setPassNum(View view) {
        if (passwordTextView.getText().length() < 6) {
            switch (view.getId()) {
                case R.id.button7:
                    passwordTextView.setText(passwordTextView.getText() + "1");
                    break;
                case R.id.button16:
                    passwordTextView.setText(passwordTextView.getText() + "2");
                    break;
                case R.id.button17:
                    passwordTextView.setText(passwordTextView.getText() + "3");
                    break;
                case R.id.button18:
                    passwordTextView.setText(passwordTextView.getText() + "4");
                    break;
                case R.id.button19:
                    passwordTextView.setText(passwordTextView.getText() + "5");
                    break;
                case R.id.button20:
                    passwordTextView.setText(passwordTextView.getText() + "6");
                    break;
                case R.id.button21:
                    passwordTextView.setText(passwordTextView.getText() + "7");
                    break;
                case R.id.button22:
                    passwordTextView.setText(passwordTextView.getText() + "8");
                    break;
                case R.id.button23:
                    passwordTextView.setText(passwordTextView.getText() + "9");
                    break;
            }
        }

        if (view.getId() == R.id.button4){
            errorMessageText.setText("");
            passwordTextView.setText(passwordTextView.getText().toString().substring(0, passwordTextView.getText().toString().length() - 1));
        }

        if (passwordTextView.getText().length() > 0) {
            button.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
            saveBtn.setVisibility(View.INVISIBLE);
        }

    }

    public void savePassword(View view) {
        if (getIntent().getSerializableExtra("password").equals("check")){
            String pswd = sharedPreferences.getString("pswd", "");
            if (Objects.equals(pswd, (String) passwordTextView.getText())) {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                errorMessageText.setText("???????????????? ????????????");
            }
        } else if (getIntent().getSerializableExtra("password").equals("write")) {
            editor = sharedPreferences.edit();
            editor.putString("pswd", (String) passwordTextView.getText());
            editor.apply();

            finish();
        } else if (getIntent().getSerializableExtra("password").equals("remove")) {
            editor = sharedPreferences.edit();
            String pswd = sharedPreferences.getString("pswd", "");
            if (Objects.equals(pswd, (String) passwordTextView.getText())) {
                editor.putString("pswd", "");
                editor.apply();

                finish();
            } else {
                errorMessageText.setText("???????????????? ????????????");
            }
        } else if (getIntent().getSerializableExtra("password").equals("check-activity")) {
            String pswd = sharedPreferences.getString("pswd", "");
            if (Objects.equals(pswd, (String) passwordTextView.getText())) {
                Intent returnResultIntent = new Intent();
                returnResultIntent.putExtra("result", true);
                setResult(Activity.RESULT_OK, returnResultIntent);
                finish();
            } else {
                errorMessageText.setText("???????????????? ????????????");
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}