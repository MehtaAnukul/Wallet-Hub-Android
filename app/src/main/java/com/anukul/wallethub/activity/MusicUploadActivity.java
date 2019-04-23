package com.anukul.wallethub.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.model.LabelModel;
import com.anukul.wallethub.model.MusicModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

import static com.anukul.wallethub.AppConstant.RESULT_LOAD_IMAGE;

public class MusicUploadActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private Spinner labelSpineer;
    private Button selectMusicBtn;
    private Button uploadMusicBtn;
    private ArrayList<MusicModel> musicModelArrayList;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> labelStringArrayList;
    private FirebaseAuth firebaseAuth;
    private ArrayList<LabelModel> labelModelArrayList;
    private String labelPushKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_upload);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        musicModelArrayList = new ArrayList<>();
        labelModelArrayList = new ArrayList<>();
        labelStringArrayList = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Music");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();


        arrayAdapter = new ArrayAdapter<String>(MusicUploadActivity.this,
                android.R.layout.simple_list_item_1, labelStringArrayList);
        labelSpineer.setAdapter(arrayAdapter);
        getLabelData();

    }

    private void getLabelData() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                .child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot labelModels : dataSnapshot.getChildren()) {
                    LabelModel labelModel = labelModels.getValue(LabelModel.class);
                    labelModel.setPushKey(labelModels.getKey());
                    labelStringArrayList.add(labelModel.getLabelName());
                    labelModelArrayList.add(labelModel);
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void initView() {
        labelSpineer = findViewById(R.id.activity_musicUpload_labelSpinner);
        selectMusicBtn = findViewById(R.id.activity_musicUpload_selectMusicBtn);
        uploadMusicBtn = findViewById(R.id.activity_musicUpload_uploadBtn);

        selectMusicBtn.setOnClickListener(this);
        uploadMusicBtn.setOnClickListener(this);
        labelSpineer.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_musicUpload_selectMusicBtn:
                selectMusicProcess();
                break;
            case R.id.activity_musicUpload_uploadBtn:
                uploadMusicProcess();
                break;
        }
    }

    private void uploadMusicProcess() {

        for (int i = 0; i < musicModelArrayList.size(); i++) {
            uploadAudioToStorage(i, musicModelArrayList.get(i));
        }


    }

    private void uploadAudioToStorage(int i, final MusicModel musicModel) {

        final StorageReference audioToUpload = storageReference.child("Audio/" + UUID.randomUUID());
        final StorageReference thumbToUpload = storageReference.child("Thumb/" + UUID.randomUUID());

        final int finalI = i;
        audioToUpload
                .putFile(musicModel.getAudio()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                audioToUpload.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri audiouri) {

                                Log.e("AUDIO", audiouri + "");

                                musicModel.setAudioUrl(audiouri.toString());


//                                insertDataInToDatabase(uri.toString());
                            }
                        });


                thumbToUpload.putBytes(musicModel.getAudioThumbailUri())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                thumbToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.e("AUDIO1", uri + "");
                                        musicModel.setLabelPush(labelPushKey);
                                        musicModel.setAudioThumbailUrl(uri.toString());
                                        insertDataInToDatabase(musicModel);

                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                selectMusicBtn.setText(progress + " YES");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FAIL123", e.getCause() + e.getMessage() + "");
                            }
                        });


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                selectMusicBtn.setText(progress + "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FAIL123", e.getCause() + e.getMessage() + "");
            }
        });

    }

    private void insertDataInToDatabase(MusicModel musicModel) {

        String uuid = firebaseAuth.getCurrentUser().getUid();

        databaseReference
                .child(AppConstant.FIREBASE_NODE_AUDIO)
                .child(uuid)
                .push()
                .setValue(new MusicModel(musicModel.getAudioUrl(),
                        musicModel.getAudioTitle(),
                        musicModel.getAudioDuration(),
                        musicModel.getAudioThumbailUrl(),
                        musicModel.getAudioSize(),
                        musicModel.getAudioType(),
                        musicModel.getLabelPush()), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toast.makeText(MusicUploadActivity.this, "Error :" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MusicUploadActivity.this, "Images Upload", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void selectMusicProcess() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        } //creating an intent for file chooser


        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Audios"), RESULT_LOAD_IMAGE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int totalItemsSelected = data.getClipData().getItemCount();
                for (int i = 0; i < totalItemsSelected; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    musicModelArrayList.add(getMetaData(fileUri));
                }

            } else if (data.getData() != null) {
                musicModelArrayList.add(getMetaData(data.getData()));
            }
        }
    }


    public MusicModel getMetaData(Uri uri) {

        MediaMetadataRetriever m_metaRetriever = new MediaMetadataRetriever();
        m_metaRetriever.setDataSource(MusicUploadActivity.this, uri);

        byte[] audioThumbByte = m_metaRetriever.getEmbeddedPicture();


        long audioSize = getRealSizeFromUri(MusicUploadActivity.this, uri) / (1024 * 1024);
        String audioSizeMB = audioSize + "MB";

        String audioTitle = m_metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);


        //Duration code
        String audioDurationRaw = m_metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(audioDurationRaw);
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        String audioDuration = hours + ":" + minutes + ":" + seconds;


        String audioType = m_metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);


        if (!audioTitle.isEmpty() || !audioDuration.isEmpty() || audioThumbByte.length != 0 || !audioSizeMB.isEmpty() || !audioType.isEmpty())
        {
            return new MusicModel(uri, audioTitle, audioDuration, audioThumbByte, audioSizeMB, audioType);

        }else
        {

            return new MusicModel();
        }
    }

    private long getRealSizeFromUri(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Audio.Media.SIZE};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            cursor.moveToFirst();
            return Long.parseLong(cursor.getString(column_index));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        labelPushKey = labelModelArrayList.get(position).getPushKey();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
