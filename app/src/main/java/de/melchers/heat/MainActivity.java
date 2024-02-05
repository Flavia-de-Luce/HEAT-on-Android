package de.melchers.heat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;

import de.melchers.heat.classes.ExcelExporter;
import de.melchers.heat.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final String LAST_OPENED_URI_KEY = "de.melchers.heat.actionopendocument.pref.LAST_OPENED_URI_KEY";
    private final String TAG = "MainActivity";

    private final String OPEN_DOCUMENT_REQUEST_CODE = "0x33"; // Könnte alles mögliche sein, wird nur zum zuordnen der Daten gebraucht.
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                }
            });
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        ActivityCompat.requestPermissions(this, new String[]{
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        askForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, 11);
        askForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 12);

    }

    private static final int PICK_PDF_FILE = 2;

    public void createFile(View view) { //Uri pickerInitialUri
        ExcelExporter.export();
        //        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);//, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
//        //intent.addCategory(Intent.CATEGORY_OPENABLE);
//        //intent.setType("application/msexcel");
//        intent.setDataAndType(MediaStore.Downloads.EXTERNAL_CONTENT_URI, "application/msexcel");
//        intent.putExtra(Intent.EXTRA_TITLE, "test.xls");
//
//        // Optionally, specify a URI for the directory that should be opened in
//        // the system file picker when your app creates the document.
//        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
//
//        //startActivityForResult(intent, CREATE_FILE);
//        this.startActivity(intent);
    }

    public void openFile(View view) {
        File sd =  new File("/storage/emulated/0/download/testExporter.xlsx");
        ExcelExporter.importXLSX("testExporter.xlsx",sd , new File(sd.getAbsolutePath()));
//        //Uri downloads = "/storage/emulated/0/Download";
//        // Uri apkURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext(), file);
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        //Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setDataAndType(MediaStore.Downloads.EXTERNAL_CONTENT_URI, "application/*");
//        //intent.setType("application/pdf");
//
//        // Optionally, specify a URI for the file that should appear in the
//        // system file picker when it loads.
//        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
//        this.startActivity(intent);
//        //startActivityForResult(intent, PICK_PDF_FILE);
    }
    // MIME Type for Excel application/msexcel

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, permission + " is already granted.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}