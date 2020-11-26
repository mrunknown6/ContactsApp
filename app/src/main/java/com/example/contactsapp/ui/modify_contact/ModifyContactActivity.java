package com.example.contactsapp.ui.modify_contact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contactsapp.BaseActivity;
import com.example.contactsapp.R;
import com.example.contactsapp.db.Image;
import com.example.contactsapp.db.ImageDatabase;
import com.example.contactsapp.ui.dashboard.models.ContactModel;
import com.example.contactsapp.util.BitmapUtil;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ModifyContactActivity extends BaseActivity {

    TextInputLayout tilFirstName, tilLastName, tilPhoneNumber;
    ImageView ivToggle, civImageContact;

    ContactModel contactModel;
    Bitmap selectedImage;

    private static int REQUEST_CODE_CHOOSE_IMAGE = 1005;
    private static int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1006;

    // determines what to perform (add or update)
    private String operation;

    private ModifyContactViewModel viewModel;

    private static int REQUEST_CODE_WRITE_CONTACT = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_contact);

        viewModel = new ViewModelProvider(this).get(ModifyContactViewModel.class);

        operation = getIntent().getExtras().getString("operation");

        if (operation.equals("add")) {
            setupToolbar("Add contact");
        } else if (operation.equals("update")) {
            setupToolbar("Update Contact");
            contactModel = getIntent().getExtras().getParcelable("contact");
        }

        init();
        setupSession();
    }

    private void setupToolbar(String title) {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void init() {
        // initialize widget references
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        ivToggle = findViewById(R.id.ivToggle);
        civImageContact = findViewById(R.id.civImageContact);

        // set button click listener
        ivToggle.setOnClickListener(v -> {
            changeToggleIcon();
            toggleEditTexts();
        });

        // set image view click listener
        civImageContact.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_EXTERNAL_STORAGE
                );
            } else {
                openGallery();
            }
        });

        if (operation.equals("update")) {
            // put contact model info into edit texts
            tilFirstName.getEditText().setText(contactModel.getFirstName());
            tilLastName.getEditText().setText(contactModel.getLastName());
            tilPhoneNumber.getEditText().setText(contactModel.getPhoneNumber());

            // set image (if present) to the image view
            new Thread(() -> {
                try {
                    byte[] byteArray = ImageDatabase.newInstance(getApplicationContext()).getImageDao().getImage(contactModel.getId()).byteArray;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    runOnUiThread(() -> {
                        Glide.with(this)
                                .load(bitmap)
                                .into(civImageContact);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();

        }

        toggleEditTexts();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_CODE_CHOOSE_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);

                // if we are just updating the photo
                if (operation.equals("update")) {
                    Bitmap temp = BitmapUtil.getResizedBitmap(selectedImage);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    temp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    temp.recycle();

                    new Thread(
                            () -> {
                                ImageDatabase.newInstance(getApplicationContext()).getImageDao().insert(new Image(contactModel.getId(), byteArray));
                            }
                    ).start();

                }


                Glide.with(this)
                        .load(imageUri)
                        .into(civImageContact);
//                civImageContact.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void changeToggleIcon() {
        if (ivToggle.getTag().equals("edit")) {

            // change icon and tag
            ivToggle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check));
            ivToggle.setTag("check");
        } else if (ivToggle.getTag().equals("check")) {

            // change icon and tag
            ivToggle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit));
            ivToggle.setTag("edit");

            saveData();
        }
    }

    private void saveData() {
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_WRITE_CONTACT
            );
        } else {

            String firstName = tilFirstName.getEditText().getText().toString();
            String lastName = tilLastName.getEditText().getText().toString();
            String phoneNumber = tilPhoneNumber.getEditText().getText().toString();

            if (firstName.length() != 0 && phoneNumber.length() != 0) {
                ContentResolver cr = getContentResolver();

                if (operation.equals("update")) {
                    if (lastName.length() != 0)
                        viewModel.updateName(cr, contactModel.getId(), firstName.concat(" ").concat(lastName));
                    else
                        viewModel.updateName(cr, contactModel.getId(), firstName);
                    viewModel.updatePhoneNumber(cr, contactModel.getId(), phoneNumber);

                    ContactModel returnContact = new ContactModel(contactModel.getId(), firstName, lastName, phoneNumber);
                    Intent intent = new Intent();
                    intent.putExtra("contact", returnContact);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (operation.equals("add")) {
                    if (lastName.length() != 0)
                        viewModel.addContact(cr, firstName.concat(" ").concat(lastName), phoneNumber);
                    else
                        viewModel.addContact(cr, firstName, phoneNumber);

                    // insert image if present
                    if (selectedImage != null) {
                        Bitmap temp = BitmapUtil.getResizedBitmap(selectedImage);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        temp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        temp.recycle();

                        // get contact id so we can add the image to the db
                        String contactId = viewModel.getId(getContentResolver(), phoneNumber);
                        new Thread(() -> {
                            ImageDatabase.newInstance(getApplicationContext()).getImageDao().insert(new Image(contactId, byteArray));
                        }).start();


                    }

                    setResult(RESULT_OK, new Intent().putExtra("phone_number", phoneNumber));
                    Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode != REQUEST_CODE_READ_EXTERNAL_STORAGE)
                saveData();
            else
                openGallery();

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleEditTexts() {
        if (tilFirstName.getEditText().getInputType() == InputType.TYPE_NULL) {

            // make all edit texts editable
            tilFirstName.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            tilLastName.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            tilPhoneNumber.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        } else {

            // make all edit texts uneditable
            tilFirstName.getEditText().setInputType(InputType.TYPE_NULL);
            tilLastName.getEditText().setInputType(InputType.TYPE_NULL);
            tilPhoneNumber.getEditText().setInputType(InputType.TYPE_NULL);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}