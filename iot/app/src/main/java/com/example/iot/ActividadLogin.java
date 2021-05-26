package com.example.iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActividadLogin extends AppCompatActivity {

    EditText user, clave;
    Button boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_login);
        boton = findViewById(R.id.btnLogin);

        user = findViewById(R.id.txtUser);
        clave = findViewById(R.id.txtClave);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user.getText().toString().equals("mike")){
                    Toast.makeText(ActividadLogin.this, "Usuario o Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(ActividadLogin.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}