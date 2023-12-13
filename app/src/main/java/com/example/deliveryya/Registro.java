package com.example.deliveryya;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.deliveryya.models.CUsuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import android.content.ContentValues;
import android.widget.Button;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.TextView;
import android.widget.Toast;

public class Registro extends AppCompatActivity {

    // Declaración de vistas y objetos necesarios
    TextInputEditText emailText, passwordText, nombreText, apellidoText;
    TextView loginAhora;
    ProgressBar progressBarRegistro;
    FirebaseAuth mAuth;
    Button btnRegistro;

    FirebaseDatabase bd = FirebaseDatabase.getInstance();
    DatabaseReference usuariosRef = bd.getReference("usuario");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicialización de Firebase Authentication
        mAuth= FirebaseAuth.getInstance();

        // Enlace de vistas con elementos de la interfaz de usuario
        loginAhora = findViewById(R.id.txtLoginAhora);
        progressBarRegistro = findViewById(R.id.progressBarRegistro);
        btnRegistro = findViewById(R.id.btnRegistro);
        emailText = findViewById(R.id.email);
        nombreText = findViewById(R.id.txtInputNombre);
        passwordText = findViewById(R.id.password);
        apellidoText = findViewById(R.id.txtInputApellido);




        // Configuración del evento onClick para el enlace a la pantalla de inicio de sesión
        loginAhora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // Configuración del evento onClick para el botón de registro
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarRegistro.setVisibility(View.VISIBLE);

                String email, password;
                email = emailText.getText().toString();
                password = passwordText.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Registro.this, "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Registro.this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarRegistro.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Registro exitoso, guarda los datos del usuario en la base de datos
                                    String nombre = nombreText.getText().toString();
                                    String apellido = apellidoText.getText().toString();
                                    String correo = emailText.getText().toString();

                                    CUsuario usuario = new CUsuario(nombre, apellido, correo);
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        String userId = currentUser.getUid();
                                        usuariosRef.child(userId).setValue(usuario);

                                        Toast.makeText(Registro.this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show();
                                        Intent intent =  new Intent(Registro.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Si no se puede obtener el usuario actual, muestra un mensaje de error
                                        Toast.makeText(Registro.this, "Error al obtener el usuario actual", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Registro no exitoso, muestra un mensaje de error
                                    Toast.makeText(Registro.this, "Creación de usuario no exitosa.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Comprueba si el usuario está autenticado y actualiza la interfaz de usuario en consecuencia
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*if(currentUser != null){
            // Si el usuario ya está autenticado, redirige a otra actividad (comentado en el código original)
            Intent intent = new Intent(getApplicationContext(), ProductosPostres.class);
            startActivity(intent);
            finish();
        }*/
    }
}
