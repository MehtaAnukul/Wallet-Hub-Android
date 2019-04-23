package com.anukul.wallethub.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.adapter.NotesCustomAdapter;
import com.anukul.wallethub.listener.ItemClickListener;
import com.anukul.wallethub.model.LabelModel;
import com.anukul.wallethub.model.NoteModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotesViewActivity extends AppCompatActivity implements ItemClickListener {
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private RecyclerView viewNoteRecyclerView;
    private ArrayList<NoteModel> noteModelArrayList;
    private NotesCustomAdapter notesCustomAdapter;
    private FloatingActionButton floatingActionButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__notes);

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Note");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        viewNoteRecyclerView = findViewById(R.id.activity_viewNote_recyclerView);

        noteModelArrayList = new ArrayList<>();

        notesCustomAdapter = new NotesCustomAdapter(noteModelArrayList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        viewNoteRecyclerView.setLayoutManager(layoutManager);
        viewNoteRecyclerView.setAdapter(notesCustomAdapter);


        floatingActionButton = findViewById(R.id.view_notesfloatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoAddNoteActivity = new Intent(NotesViewActivity.this, NoteAddActivity.class);
                gotoAddNoteActivity.putExtra(AppConstant.KEY_CODE, false);
                startActivity(gotoAddNoteActivity);
            }
        });

        getNotesData();
    }

    private void getNotesData() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        if (!uuid.isEmpty()) {
            databaseReference
                    .child(AppConstant.FIREBASE_NODE_NOTE)
                    .child(uuid)
                    .addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            noteModelArrayList.clear();

                            Log.e("NOTE", dataSnapshot.getChildrenCount() + "");
                            Log.e("NOTE", dataSnapshot + "");
//                            for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
//                            }

                            for (DataSnapshot imageModel : dataSnapshot.getChildren()) {
                                Log.e("NOTE1", imageModel + "");

                                NoteModel noteModel = imageModel.getValue(NoteModel.class);
                                noteModel.setPushKey(imageModel.getKey());
                                Log.e("YESTHIS", imageModel.getKey() + "");
                                noteModelArrayList.add(noteModel);

                            }

                            notesCustomAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

        } else {
            Toast.makeText(this, "Token Error Relogin and Try Again", Toast.LENGTH_SHORT).show();
        }


    }

    //LabelCustom card optiom menu (update,delete)
    @Override
    public void onItemClick(final NoteModel notesModel, View view, final int position) {


        switch (view.getId()) {
            case R.id.notes_custom_layout_optionMenu:

                PopupMenu popupMenu = new PopupMenu(this, view);
                getMenuInflater().inflate(R.menu.custom_option_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.custom_option_menu_update:
                                updateData(position, notesModel);
                                break;
                            case R.id.custom_option_menu_delete:
                                deleteData(position, notesModel);
                                break;
                        }
                        return false;
                    }
                });
                break;
            default:
                Toast.makeText(this, ""+notesModel.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }


    }

    private void deleteData(final int position, final NoteModel notesModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesViewActivity.this, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        builder.setTitle("DELETE");
        builder.setMessage("Are you sure");
        builder.setCancelable(false);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NotesViewActivity.this, "Notes Deleted", Toast.LENGTH_SHORT).show();
                noteModelArrayList.remove(position);
                notesCustomAdapter.notifyDataSetChanged();
                Log.e("DELTE", notesModel.getPushKey());

                final String uuid = firebaseAuth.getCurrentUser().getUid();

                if (!uuid.isEmpty()) {
                    databaseReference.child(AppConstant.FIREBASE_NODE_NOTE)
                            .child(uuid)
                            .child(notesModel.getPushKey())
                            .removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Toast.makeText(NotesViewActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotesViewActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NotesViewActivity.this, "Notes Not Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void updateData(int position, NoteModel notesModel) {
        Intent updateIntent = new Intent(NotesViewActivity.this, NoteAddActivity.class);
        updateIntent.putExtra(AppConstant.KEY_CODE, true);
        updateIntent.putExtra(AppConstant.KEY_PUSHKEY_NOTES, notesModel.getPushKey());
        Log.e("UpdatePostion", "" + position);
        startActivityForResult(updateIntent, 100);
    }



    @Override
    public void onItemClickLabel(LabelModel labelModel, View view, int position) {
    }

}
