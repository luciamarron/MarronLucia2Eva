package com.example.marronlucia2eva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PantallaLogin extends Prefencias {

    private FirebaseAuth mAuth;
    private Switch change_theme;
    private ConstraintLayout back;
    Button inicio, registro;
    EditText correo, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        inicio = (Button) findViewById(R.id.inicio);
        registro = (Button) findViewById(R.id.registro);
        correo = (EditText) findViewById(R.id.usuario);
        password = (EditText) findViewById(R.id.contraseña);
        change_theme = (Switch) findViewById(R.id.switch1);
        back = (ConstraintLayout)findViewById(R.id.back);

        //PREFERENCIAS
        SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        int theme = sp.getInt("Theme",1);
        if(theme==1){
            change_theme.setChecked(false);
        }else{
            change_theme.setChecked(true);
        }

        change_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(change_theme.isChecked()){
                    editor.putInt("Theme",0);
                }else{
                    editor.putInt("Theme",1);

                }
                editor.commit();
                setDayNight();
            }
        });

        //BOTÓN INICIO DE SESIÓN
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(correo.getText().toString(), password.getText().toString());
            }
        });

        //BOTÓN REGISTRO DE NUEVO USUARIO
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar(correo.getText().toString(), password.getText().toString());
            }
        });

    }

    public void login(String correo, String contrasena){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(PantallaLogin.this, "Authentication succesful.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PantallaLogin.this, Agenda.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(PantallaLogin.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registrar(String correo, String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(PantallaLogin.this, "Registration succesful.",
                            Toast.LENGTH_SHORT).show();

                }else if (mAuth.getCurrentUser().getEmail().equals(correo)){
                    Toast.makeText(PantallaLogin.this, "El usuario ya está registrado",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if(password.length()<6){
                        Toast.makeText(PantallaLogin.this, "La contraseña debe tener 6 caracteres como mínimo",
                                Toast.LENGTH_SHORT).show();
                    }

                    if(!correoValido(correo)){
                        Toast.makeText(PantallaLogin.this, "Email inválido",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //MÉTODO QUE COMPRUEBA QUE EL CORREO TIENE UN PATRÓN VÁLIDO
    public static boolean correoValido (String correo){
        Pattern pattern = Pattern.compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");
        Matcher marcher = pattern.matcher(correo);
        return marcher.matches();
    }

}