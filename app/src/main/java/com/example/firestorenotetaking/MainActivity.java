package com.example.firestorenotetaking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PRIORITY = "priority";

    private EditText editTextTitle, editTextDescription, editTextPriority;
    private TextView textViewdata;

    private FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = dbInstance.collection("Notebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPriority = findViewById(R.id.edit_text_priority);
        textViewdata = findViewById(R.id.text_view_data);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // check the changes in the database, when the activity starts
        // why event ? because a change in the database is an event
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Note note = queryDocumentSnapshot.toObject(Note.class);
                    note.setDocumentId(queryDocumentSnapshot.getId());
                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int priority = note.getPriority();

                    data += "ID: " + documentId + "\npriority: " + priority + "\ntitle: " + title + "\ndescription: " + description + "\n\n";
                }
                textViewdata.setText(data);
            }
        });
    }


    public void addNote(View view) {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0");
        }
        int priority = Integer.parseInt(editTextPriority.getText().toString().trim());
        Note noteObj = new Note(title, description, priority);
        notebookRef.add(noteObj);

    }

    public void loadNotes(View view) {
        Task task1 = notebookRef.whereGreaterThan("priority", 2)
                .orderBy("priority")
                .get();
        Task task2 = notebookRef.whereLessThan("priority", 2)
                .orderBy("priority")
                .get();

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);

        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                String data = "";
                for(QuerySnapshot querySnapshot : querySnapshots) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                        Note note = queryDocumentSnapshot.toObject(Note.class);
                        note.setDocumentId(queryDocumentSnapshot.getId());
                        String documentId =  note.getDocumentId();
                        String title = note.getTitle();
                        String description = note.getDescription();
                        int priority = note.getPriority();

                        data += "ID: " + documentId + "\ntitle: " + title + "\npriority: " + priority + "\n\n" ;
                    }
                }
                textViewdata.setText(data);
            }
        });

    }

//   public void deleteDescription(View view) {
//        /* Way 1
//     ************************************************
//        Map<String, Object > note = new HashMap<>();
//        note.put(KEY_DESCRIPTION, FieldValue.delete());
//        noteRef.update(note);   */
//
//        noteRef.update(KEY_DESCRIPTION, FieldValue.delete());
//
//    }
//
//    public void updateNote(View view) {
//        String description = editTextDescription.getText().toString().trim();
//        Map<String, Object> map = new HashMap<>();
//        map.put(KEY_DESCRIPTION, description);
//
//        //noteRef.set(map, SetOptions.merge());
//        noteRef.update(map);
//        //noteRef.update(KEY_TITLE, "i was just checking");
//    }
//
//    public void deleteNote(View view) {
//        noteRef.delete();
//    }
}