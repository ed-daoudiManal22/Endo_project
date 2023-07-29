package com.example.myapplication.Community;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.databinding.ActivityPublishBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PublishActivity extends AppCompatActivity {

    ActivityPublishBinding binding;
    Uri filepath;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        selectImage();
        uploadData();
    }

    private void selectImage() {
        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Your Image"), 101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            binding.imgThumbnail.setVisibility(View.VISIBLE);
            binding.imgThumbnail.setImageURI(filepath);
            binding.view2.setVisibility(View.INVISIBLE);
            binding.bSelectImage.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadData() {
        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(PublishActivity.this)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    String title = binding.bTitle.getText().toString();
                                    String desc = binding.bDesc.getText().toString();

                                    if (title.isEmpty()) {
                                        binding.bTitle.setError("Field is Required!!");
                                    } else if (desc.isEmpty()) {
                                        binding.bDesc.setError("Field is Required!!");
                                    }  else if (filepath == null) {
                                        Toast.makeText(PublishActivity.this, "Select an Image!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ProgressDialog pd = new ProgressDialog(PublishActivity.this);
                                        pd.setTitle("Uploading...");
                                        pd.setMessage("Please wait for a while until we upload this data to our Firebase Storage and Firestore");
                                        pd.setCancelable(false);
                                        pd.show();

                                        // Get the current user's username from Firebase Authentication
                                        firebaseAuth = FirebaseAuth.getInstance();
                                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                        String userName = currentUser.getDisplayName();

                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference reference = storage.getReference().child("images/" + filepath.getLastPathSegment() + ".jpg");
                                        reference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        String file_url = task.getResult().toString();
                                                        String date = (String) DateFormat.format("dd", new Date());
                                                        String month = (String) DateFormat.format("MMM", new Date());
                                                        String final_date = date + " " + month;

                                                        HashMap<String, String> map = new HashMap<>();
                                                        map.put("title", title);
                                                        map.put("desc", desc);
                                                        map.put("author", userName);
                                                        map.put("date", final_date);
                                                        map.put("img", file_url);
                                                        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                                                        map.put("share_count", "0");

                                                        FirebaseFirestore.getInstance().collection("Blogs").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    pd.dismiss();
                                                                    Toast.makeText(PublishActivity.this, "Post Uploaded!!!", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(PublishActivity.this, HomeActivity.class);
                                                                    startActivity(intent);
                                                                    /*binding.imgThumbnail.setVisibility(View.INVISIBLE);
                                                                    binding.view2.setVisibility(View.VISIBLE);
                                                                    binding.bSelectImage.setVisibility(View.VISIBLE);
                                                                    binding.bTitle.setText("");
                                                                    binding.bDesc.setText("");
                                                                    binding.bAuthor.setText("");*/
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    showSettingDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        })
                        .withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError dexterError) {
                                finish();
                            }
                        })
                        .onSameThread()
                        .check();
            }
        });
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permission");
        builder.setMessage("This app needs permission to use this feature. You can grant us these permissions manually by clicking on the below button");
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }
}
