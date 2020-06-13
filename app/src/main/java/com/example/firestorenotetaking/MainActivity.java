package com.example.firestorenotetaking;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {


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
        notebookRef.document("gW2IgopSdLk0uIX65XAr")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        notebookRef.orderBy("priority")
                                .startAfter(documentSnapshot)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        String data = "";
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Note note = documentSnapshot.toObject(Note.class);
                                            note.setDocumentId(documentSnapshot.getId());
                                            String title = note.getTitle();
                                            String description = note.getDescription();
                                            String documentId = note.getDocumentId();
                                            int priority = note.getPriority();

                                            data += "ID : " + documentId + "\nTitle : " + title + "\nDecsiption : "
                                                    + description + "\nPriorty : " + priority + "\n\n";
                                        }
                                        textViewdata.setText(data);
                                    }
                                });
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