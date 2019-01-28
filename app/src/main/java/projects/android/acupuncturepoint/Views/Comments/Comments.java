package projects.android.acupuncturepoint.Views.Comments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projects.android.acupuncturepoint.R;

public class Comments extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Button btnAdd;
    private ListView list;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
//        btnAdd = findViewById(R.id.btnAdd);
        list = findViewById(R.id.list);
        strings = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
        list.setAdapter(arrayAdapter);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.getReference("accupuncture");

//        myRef.setValue("Hello, World!");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("====", "Failed to read value.", error.toException());
            }
        });
        mDatabase.child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue().toString();
                    Log.d("========", value);
                    strings.add(value);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                strings.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String value = snapshot.getValue().toString();
                    Log.d("========", value);
                    strings.add(value);
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            LayoutInflater layoutInflater = getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.add_comment, null);
            AlertDialog alertDialog = new AlertDialog.Builder(Comments.this).create();
            alertDialog.setTitle("Ý kiến");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Ý kiến của bạn");


            final EditText etComments = (EditText) view.findViewById(R.id.etComments);

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Gửi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!etComments.getText().toString().trim().isEmpty()) {
                        writeNewUser(etComments.getText().toString().trim());
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ý kiến không được trống, vui lòng nhập!", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });


            alertDialog.setView(view);
            alertDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void writeNewUser(String body) {
        String userId = mDatabase.push().getKey();
        if (userId != null) {
            mDatabase.child("comments").child(userId).setValue(body);
        }
    }
}
