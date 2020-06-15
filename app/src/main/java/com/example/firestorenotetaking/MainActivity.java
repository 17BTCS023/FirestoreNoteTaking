package com.example.firestorenotetaking;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

public class MainActivity extends AppCompatActivity {


    private EditText editTextTitle, editTextDescription, editTextPriority;
    private TextView textViewdata;

    private FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = dbInstance.collection("Notebook");

    DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPriority = findViewById(R.id.edit_text_priority);
        textViewdata = findViewById(R.id.text_view_data);

        executeTransaction();

    }

    // ALL OR NOTHING APPROACH

    private void executeTransaction() {
        dbInstance.runTransaction(new Transaction.Function<Long>() {
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference exampleNoteRef = notebookRef.document("New note");
                DocumentSnapshot newNoteSnapshot = transaction.get(exampleNoteRef);
                Long newPriority = newNoteSnapshot.getLong("priority") + 1;
                transaction.update(exampleNoteRef, "priority", newPriority);
                return newPriority;
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {

            @Override
            public void onSuccess(Long result) {
                Toast.makeText(MainActivity.this, "Priority: "+ result, Toast.LENGTH_SHORT).show();
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
        Query query;
        if(lastResult == null){
            query = notebookRef.orderBy("priority")
                    .limit(3);
        }else{
            query = notebookRef.orderBy("priority")
                    .startAfter(lastResult)
                    .limit(3);
        }
       query.get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    String data = "";
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());
                        String title = note.getTitle();
                        String description = note.getDescription();
                        String documentId  = note.getDocumentId();
                        int priority = note.getPriority();

                        data+= "ID : " + documentId + "\nTitle : " + title + "\nDecsiption : "
                                + description + "\nPriorty : " + priority + "\n\n";
                    }
                    if(queryDocumentSnapshots.size() >0) {
                        data += "_____________________\n\n";
                        textViewdata.append(data);
                        lastResult = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                    }
                   }
               });
    }

}