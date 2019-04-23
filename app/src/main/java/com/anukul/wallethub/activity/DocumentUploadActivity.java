package com.anukul.wallethub.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.model.DocumentModel;
import com.anukul.wallethub.model.LabelModel;
import com.anukul.wallethub.model.MusicModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import static com.anukul.wallethub.AppConstant.RESULT_LOAD_IMAGE;

public class DocumentUploadActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private Spinner labelSpineer;
    private Button selectDocumentsBtn;
    private Button uploadDocumentsBtn;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private ArrayList<String> labelStringArrayList;
    private ArrayList<LabelModel> labelModelArrayList;
    private ArrayAdapter<String> arrayAdapter;
    private String labelPushKey;
    private ArrayList<DocumentModel> documentModelArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        labelModelArrayList = new ArrayList<>();
        labelStringArrayList = new ArrayList<>();

        documentModelArrayList = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Document");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        arrayAdapter = new ArrayAdapter<String>(DocumentUploadActivity.this,
                android.R.layout.simple_list_item_1, labelStringArrayList);
        labelSpineer.setAdapter(arrayAdapter);
        getLabelData();

    }

    private void initView() {

        labelSpineer = findViewById(R.id.activity_documentUpload_labelSpinner);
        selectDocumentsBtn = findViewById(R.id.activity_documentUpload_selectdocumentsBtn);
        uploadDocumentsBtn = findViewById(R.id.activity_documentsUpload_uploadBtn);

        selectDocumentsBtn.setOnClickListener(this);
        uploadDocumentsBtn.setOnClickListener(this);

        labelSpineer.setOnItemSelectedListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_documentUpload_selectdocumentsBtn:
                selectDocumentsProcess();
                break;
            case R.id.activity_documentsUpload_uploadBtn:
                uploadDocumentsProcess();
                finish();
                break;
        }
    }

    private void uploadDocumentsProcess() {

        for (int i = 0; i < documentModelArrayList.size(); i++) {
            uploadAudioToStorage(i, documentModelArrayList.get(i));
        }


    }

    private void uploadAudioToStorage(int i, final DocumentModel documentModel) {

        final StorageReference documentToUpload = storageReference.child("Document/" + UUID.randomUUID());

        final int finalI = i;


        documentToUpload
                .putFile(documentModel.getFileUri())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        documentToUpload
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        documentModel.setFileUrl(uri.toString());
                                        insertDataInToDatabase(documentModel);

                                    }
                                });
                    }
                });


    }

    private void insertDataInToDatabase(DocumentModel documentModel) {

        String uuid = firebaseAuth.getCurrentUser().getUid();

        databaseReference
                .child(AppConstant.FIREBASE_NODE_DOCUMENT)
                .child(uuid)
                .push()
                .setValue(new DocumentModel(
                                documentModel.getFileUrl(),
                                documentModel.getFileSize(),
                                documentModel.getFileType(),
                                documentModel.getFileName(),
                                labelPushKey)
                        , new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Toast.makeText(DocumentUploadActivity.this, "Error :" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DocumentUploadActivity.this, "Document Upload", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

    }

    private void selectDocumentsProcess() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
        // creating an intent for file chooser

        Intent intent = new Intent();
        intent.setType("application/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), RESULT_LOAD_IMAGE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int totalItemsSelected = data.getClipData().getItemCount();
                for (int i = 0; i < totalItemsSelected; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    documentModelArrayList.add(getMetaData(fileUri));
                }

            } else if (data.getData() != null) {
                documentModelArrayList.add(getMetaData(data.getData()));
            }
        }
    }

    private DocumentModel getMetaData(Uri fileUri) {

        String fileName = uri2filename(fileUri);

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String fileType = mime.getExtensionFromMimeType(cR.getType(fileUri));


        long size = getRealSizeFromUri(DocumentUploadActivity.this, fileUri) / 1024;

        String fileSize = "";

        fileSize = size + "KB";

        if (size > 1024) {
            size = size / 1024;
            fileSize = size + "MB";
        }

        return new DocumentModel(fileUri, fileSize, fileType, fileName);

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

    private String uri2filename(Uri uri) {

        String ret = null;
        String scheme = uri.getScheme();

        if (scheme.equals("file")) {
            ret = uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                ret = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return ret;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        labelPushKey = labelModelArrayList.get(position).getPushKey();
       // Toast.makeText(this, "" + labelPushKey, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
