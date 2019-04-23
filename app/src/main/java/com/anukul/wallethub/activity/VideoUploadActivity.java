package com.anukul.wallethub.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.anukul.wallethub.model.VideoModel;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class VideoUploadActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int RESULT_LOAD_VIDEO = 200;
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private Spinner labelSpineer;
    private Button selectVideosBtn;
    private Button uploadVideosBtn;

    private String labelPush;

    private ArrayList<VideoModel> videoModelArrayList;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<LabelModel> labelModelArrayList;
    private ArrayList<String> labelStringArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Video");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        labelModelArrayList = new ArrayList<>();
        labelStringArrayList = new ArrayList<>();
        videoModelArrayList = new ArrayList<>();

        initView();

        arrayAdapter = new ArrayAdapter<String>(VideoUploadActivity.this,
                android.R.layout.simple_list_item_1, labelStringArrayList);
        labelSpineer.setAdapter(arrayAdapter);
        getLabelData();

    }

    private void initView() {

        labelSpineer = findViewById(R.id.activity_videoUpload_labelSpinner);
        selectVideosBtn = findViewById(R.id.activity_videoUpload_selectVideoBtn);
        uploadVideosBtn = findViewById(R.id.activity_videoUpload_uploadBtn);

        selectVideosBtn.setOnClickListener(this);
        uploadVideosBtn.setOnClickListener(this);

        labelSpineer.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.activity_videoUpload_selectVideoBtn:
                selectVideoProcess();
                break;
            case R.id.activity_videoUpload_uploadBtn:
                uploadVideoProcess();
//                progressDialog.dismiss();
                break;
        }
    }

    private void uploadVideoProcess() {


        for (int i = 0; i < videoModelArrayList.size(); i++) {
            uploadVideoToStorage(i, videoModelArrayList.get(i));
        }

    }

    private void uploadVideoToStorage(int i, final VideoModel videoModel) {

        final StorageReference audioToUpload = storageReference.child("Video/" + UUID.randomUUID());
        final StorageReference thumbToUpload = storageReference.child("Thumb/" + UUID.randomUUID());

        final int finalI = i;
        audioToUpload.putFile(videoModel.getVideo()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                audioToUpload.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri videouri) {


                                videoModel.setVideoUrl(videouri.toString());

//                                insertDataInToDatabase(uri.toString());
                            }
                        });


                thumbToUpload.putBytes(videoModel.getVideoThumbailByte())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                thumbToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.e("AUDIO1", uri + "");
                                        videoModel.setVideoThumbailUrl(uri.toString());
                                        insertDataInToDatabase(videoModel);

                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                selectVideosBtn.setText(progress + " YES");

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

                selectVideosBtn.setText(progress + "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FAIL123", e.getCause() + e.getMessage() + "");
            }
        });

    }

    private void insertDataInToDatabase(VideoModel videoModel) {


        String uuid = firebaseAuth.getCurrentUser().getUid();

        databaseReference
                .child(AppConstant.FIREBASE_NODE_VIDEO)
                .child(uuid)
                .push()
                .setValue(new
                        VideoModel(videoModel.getVideoUrl()
                        , videoModel.getVideoTitle()
                        , videoModel.getVideoDuration()
                        , videoModel.getVideoThumbailUrl()
                        , videoModel.getVideoSize()
                        , videoModel.getVideoType()
                        , labelPush), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        if (databaseError != null) {
                            Toast.makeText(VideoUploadActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VideoUploadActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void selectVideoProcess() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Videos"),
                RESULT_LOAD_VIDEO);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        labelPush = labelModelArrayList.get(position).getPushKey();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int totalItemsSelected = data.getClipData().getItemCount();
                for (int i = 0; i < totalItemsSelected; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    videoModelArrayList.add(getMetaData(fileUri));
                }
            } else if (data.getData() != null) {
                videoModelArrayList.add(getMetaData(data.getData()));
            }
        }
    }


    public VideoModel getMetaData(Uri uri) {

        MediaMetadataRetriever m_metaRetriever = new MediaMetadataRetriever();
        m_metaRetriever.setDataSource(VideoUploadActivity.this, uri);

        Bitmap bitmap2 = m_metaRetriever.getFrameAtTime();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        Log.e("THUMTHUM", bitmap2 + "");
//        Uri videoThumb = getImageUri(VideoUploadActivity.this, bitmap2);

        long videoSize = getRealSizeFromUri(VideoUploadActivity.this, uri) / (1024 * 1024);
        String videoSizeMB = videoSize + "MB";

        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        String videoTitle = null;
        ContentResolver cr = getApplicationContext().getContentResolver();

        Cursor metaCursor = cr.query(uri,
                projection, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    videoTitle = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }

        //Duration code
        String videoDurationRaw = m_metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(videoDurationRaw);
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        String videoDuration = hours + ":" + minutes + ":" + seconds;

        String videoType = m_metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);


        if (!videoDuration.isEmpty() ||
                data.length != 0 ||
                !videoSizeMB.isEmpty() ||
                !videoType.isEmpty()) {


            return new VideoModel(uri, videoTitle, videoDuration, data,
                    videoSizeMB, videoType);

        } else {

            return new VideoModel();
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


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
