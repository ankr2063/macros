package com.example.android.macros;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private DatabaseReference fd;
    TextView p,c,f,cl;
    String Query;
    EditText ed;
    private static final int REQUEST_CODE = 1234;
    Button show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button speak = findViewById(R.id.speakButton);
        ed = this.findViewById(R.id.editText1);
        ed.setText("");
        show=this.findViewById(R.id.imageButton);
        p = findViewById(R.id.pn);
        c = findViewById(R.id.carbs);
        f = findViewById(R.id.fats);
        cl = findViewById(R.id.cals);
        util.getmData();
        // Disable  if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            speak.setEnabled(false);
            speak.setText("Recognizer not present");
        }
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                speak.setEnabled(true);
            }

        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query=  ed.getText().toString();
                if (Query.equals("")) {
                    p.setText("0 g");
                    c.setText("0 g");
                    f.setText("0 g");
                    cl.setText("0 kcal");
                    Toast.makeText(getApplicationContext(), "Please enter a food item!!", Toast.LENGTH_LONG).show();
                } else {
                    fd = util.getmData().getReference().child("FoodItem");
                    final DatabaseReference m = fd.child(Query);
                    m.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                DatabaseReference m = fd.child(Query);
                                DatabaseReference pr = m.child("Protein");
                                pr.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String macros = dataSnapshot.getValue().toString();
                                            p.setText(macros + " g");
                                        } else {
                                            p.setText("NA");
                                            Toast.makeText(getApplicationContext(), "This item is not available...!!\n\tTry again...!!", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                DatabaseReference ca = m.child("Carbohydrates");
                                ca.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String macros = dataSnapshot.getValue().toString();
                                            c.setText(macros + " g");
                                        } else {
                                            c.setText("NA");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                DatabaseReference fa = m.child("Fats");
                                fa.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String macros = dataSnapshot.getValue().toString();
                                            f.setText(macros + " g");
                                        } else {
                                            f.setText("NA");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                DatabaseReference cal = m.child("Calories");
                                cal.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String macros = dataSnapshot.getValue().toString();
                                            cl.setText(macros + " kcal");
                                        } else {
                                            cl.setText("NA");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            } else {
                                p.setText("NA");
                                c.setText("NA");
                                f.setText("NA");
                                cl.setText("NA");
                                Toast.makeText(getApplicationContext(), "This item is not available...!!\n\tTry again...!!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
    /**
     * Handle the action of the button being clicked
     */
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }
    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, REQUEST_CODE);
    }
    /**
     * Handle the results from the voice recognition activity.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty()) {
                Query = matches.get(0);

                String[] arr = Query.split(" ");
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < arr.length; i++) {
                    sb.append(Character.toUpperCase(arr[i].charAt(0)))
                            .append(arr[i].substring(1)).append(" ");
                }
                Query = sb.toString().trim();
                if((Query.equals("Ashray Apoorva"))||(Query.equals("Ashray"))||(Query.equals("Ashraya"))||(Query.equals("Ashraya Apoorva")))
                    ed.setText("Bandar");
                else
                    ed.setText(Query);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}
