package com.example.myapplication.Community;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Fragments.Community;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.databinding.ActivityPublishBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
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
        binding.view2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Your Image"), 101);
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
        binding.btnPublish.setOnClickListener(v -> Dexter.withActivity(PublishActivity.this)
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
                                uploadDataWithProgressDialog(title, desc);
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
                .withErrorListener(dexterError -> finish())
                .onSameThread()
                .check());
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permission");
        builder.setMessage("This app needs permission to use this feature. You can grant us these permissions manually by clicking on the below button");
        builder.setPositiveButton("Next", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            finish();
        });
        builder.show();
    }
    private void uploadDataWithProgressDialog(String title, String desc) {
        ProgressDialog pd = new ProgressDialog(PublishActivity.this);
        pd.setTitle("Uploading...");
        pd.setMessage("Please wait for a while until we upload this data to our Firebase Storage and Firestore");
        pd.setCancelable(false);
        pd.show();

        // Get the current user's username from Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUid();
            String finalUserId = userId;
            FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("name");

                            // Continue with the rest of the upload process
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference reference = storage.getReference().child("images/" + filepath.getLastPathSegment() + ".jpg");
                            reference.putFile(filepath).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnCompleteListener(task -> {
                                String file_url = task.getResult().toString();
                                String date = (String) DateFormat.format("dd", new Date());
                                String month = (String) DateFormat.format("MMM", new Date());
                                String final_date = date + " " + month;

                                HashMap<String, String> map = new HashMap<>();
                                map.put("ownerId", finalUserId);
                                map.put("title", title);
                                map.put("desc", desc);
                                map.put("author", userName); // Using fetched userName
                                map.put("date", final_date);
                                map.put("img", file_url);
                                map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                                map.put("share_count", "0");

                                FirebaseFirestore.getInstance().collection("Blogs").document().set(map).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        pd.dismiss();
                                        Toast.makeText(PublishActivity.this, "Post Uploaded!!!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PublishActivity.this, Community.class);
                                        startActivity(intent);
                                    }
                                });
                            }));
                        } else {
                            pd.dismiss();
                            Toast.makeText(PublishActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
