package com.anukul.wallethub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.model.ImageModel;
import com.anukul.wallethub.model.LabelModel;
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

public class ImageUploadActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private Spinner labelSpineer;
    private Button selectImagesBtn;
    private Button uploadImageBtn;
    private ArrayList<String> labelStringArrayList;
    private ArrayList<LabelModel> labelModelArrayList;
    private ArrayList<Uri> fileUriArrayList;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ArrayAdapter<String> arrayAdapter;
    private String labelPushKey;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        labelStringArrayList = new ArrayList<>();
        labelModelArrayList = new ArrayList<>();
        fileUriArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(ImageUploadActivity.this);

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Image");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initView();

        arrayAdapter = new ArrayAdapter<>(ImageUploadActivity.this,
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
        labelSpineer = findViewById(R.id.activity_imageUpload_labelSpinner);
        selectImagesBtn = findViewById(R.id.activity_imageUpload_selectImgBtn);
        uploadImageBtn = findViewById(R.id.activity_imageUpload_uploadBtn);

        selectImagesBtn.setOnClickListener(this);
        uploadImageBtn.setOnClickListener(this);

        labelSpineer.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_imageUpload_selectImgBtn:
                selectImagesProcess();
                break;
            case R.id.activity_imageUpload_uploadBtn:
                progressDialog.setTitle("Image Upload");
                progressDialog.setMessage("Image uploading....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                uploadImageProcess();
                break;
        }
    }

    private void uploadImageProcess() {


        for (int i = 0; i < fileUriArrayList.size(); i++) {
            uploadImagesToStorage(i, fileUriArrayList.get(i));
        }

    }

    private void selectImagesProcess() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        } //creating an intent for file chooser


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int totalItemsSelected = data.getClipData().getItemCount();

                selectImagesBtn.setText("" + totalItemsSelected);

                for (int i = 0; i < totalItemsSelected; i++) {

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    //String fileName = getFileName(fileUri);

                    fileUriArrayList.add(fileUri);

                }

                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null) {
                Uri fileUri = data.getData();
                //String fileName = getFileName(fileUri);
                fileUriArrayList.add(fileUri);

                Toast.makeText(ImageUploadActivity.this, "Selected Single File", Toast.LENGTH_SHORT).show();

            }

        }

    }

    private void uploadImagesToStorage(final int i, Uri fileUri) {

        Toast.makeText(this, "" + i + "/" + fileUriArrayList.size(), Toast.LENGTH_LONG).show();


        final StorageReference fileToUpload = storageReference.child("Images/" + UUID.randomUUID());

        final int finalI = i;
        fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileToUpload.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                insertDataInToDatabase(uri.toString());
                            }
                        });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();


            }
        });
    }

    private void insertDataInToDatabase(String uri) {

        final String uuid = firebaseAuth.getCurrentUser().getUid();
        if (!uuid.isEmpty()) {
            databaseReference
                    .child(AppConstant.FIREBASE_NODE_IMAGE)
                    .child(uuid)
                    .push()
                    .setValue(new ImageModel(labelPushKey, uri), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(ImageUploadActivity.this, "Error :" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ImageUploadActivity.this, "Images Upload", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }

    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        labelPushKey = labelModelArrayList.get(position).getPushKey();
        //Toast.makeText(this, "" + labelPushKey, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
