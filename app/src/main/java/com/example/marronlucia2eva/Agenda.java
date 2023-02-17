package com.example.marronlucia2eva;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Agenda extends Prefencias {

    Button nuevo, borrar, listar, modificar;
    ImageButton llamada;
    EditText nombre, direccion, email, telefono;
    private DatabaseReference marksRef;
    private ConstraintLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userPrincipal = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Agendas");

        nuevo = (Button) findViewById(R.id.nuevo);
        borrar = (Button) findViewById(R.id.borrar);
        modificar = (Button) findViewById(R.id.modificar);
        listar = (Button) findViewById(R.id.listar);
        back = (ConstraintLayout)findViewById(R.id.back);
        llamada = (ImageButton) findViewById(R.id.imageButton);
        telefono = (EditText) findViewById(R.id.editTextPhone);

        marksRef = userRef.child("Agenda: " + userPrincipal);


        //BOTÓN PARA REALIZAR LLAMADAS
        llamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = telefono.getText().toString(); //
                if(!num.isEmpty()) {
                    Uri number = Uri.parse("tel:" + num);
                    Intent dial = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(dial);
                }else{
                    Toast.makeText(Agenda.this, "Debes escribir un número de teléfono",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //BOTÓN PARA CREAR UN NUEVO CONTACTO EN LA AGENDA
        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre = findViewById(R.id.editTextTextPersonName);
                direccion = findViewById(R.id.editTextTextPostalAddress);
                email = findViewById(R.id.editTextTextEmailAddress);
                telefono = findViewById(R.id.editTextPhone);

                Dato dat  = new Dato(nombre.getText().toString(),direccion
                            .getText().toString(),email.getText().toString(),telefono.getText().toString());
                marksRef.push().setValue(dat);

                Toast.makeText(Agenda.this, "Nuevo contacto añadido correctamente",
                Toast.LENGTH_SHORT).show();
            }
        });

        //BOTÓN PARA LISTAR LOS CONTACTOS DE LA AGENDA
        listar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView lista = findViewById(R.id.listaNombres);
                Dato dat = new Dato();
                marksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Dato dat;
                        ArrayList<String> listado = new ArrayList<>();
                        ArrayAdapter<String> adaptador;
                        for(DataSnapshot ds: snapshot.getChildren()){
                            dat = ds.getValue(Dato.class);
                            listado.add(dat.getNombre() + "\n" + dat.getDireccion() + "\n" + dat.getEmail() + "\n" + dat.getTelefono()+"\n");
                        }
                        adaptador = new ArrayAdapter<>(Agenda.this, android.R.layout.simple_list_item_1,listado);
                        lista.setAdapter(adaptador);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //BOTÓN PARA BORRAR UN CONTACTO EXISTENTE EN LA AGENDA
        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nombre = findViewById(R.id.editTextTextPersonName);

                Query q = marksRef.orderByChild("nombre").equalTo(nombre.getText().toString());

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String clave = ds.getKey();
                            marksRef.child(clave).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        //BOTÓN PARA MODIFICAR UN CONTACTO EXISTENTE EN LA AGENDA
        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nombre = findViewById(R.id.editTextTextPersonName);
                EditText direccion = findViewById(R.id.editTextTextPostalAddress);
                EditText email = findViewById(R.id.editTextTextEmailAddress);
                EditText telefono = findViewById(R.id.editTextPhone);
                Dato dat = new Dato(nombre.getText().toString(),direccion.getText().toString(),email.getText().toString(),telefono.getText().toString());

                Query q = marksRef.orderByChild("nombre").equalTo(nombre.getText().toString());
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String clave = ds.getKey();
                            marksRef.child(clave).child("direccion").setValue(direccion.getText().toString());
                            marksRef.child(clave).child("email").setValue(email.getText().toString());
                            marksRef.child(clave).child("telefono").setValue(telefono.getText().toString());
                            Toast.makeText(Agenda.this, "Contacto modificado con éxito",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }


    public static boolean correoValido (String email){
        Pattern pattern = Pattern.compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");
        Matcher marcher = pattern.matcher(email);
        return marcher.matches();
    }
}
