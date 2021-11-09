package com.example.android_mobile_app.Movesense;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android_mobile_app.BuildConfig;

import com.example.android_mobile_app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ShareLogs extends AppCompatActivity {

    TextView resultTextView;
    ListView logsFileListView;

//    private GoogleAccountCredential mCredential;
//    private static final String[] SCOPES = {DriveScopes.DRIVE};
//    private GoogleApiClient mGoogleApiClient;
//    private static final String PREF_ACCOUNT_NAME = "accountName";
//    private static final int REQUEST_ACCOUNT_PICKER = 1000;
//    private static final int REQUEST_AUTHORIZATION = 1001;
//    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
//    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
//    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1005;

    private final String LOG_TAG = ShareLogs.class.getSimpleName();

    private List<File> logsFileList = new ArrayList<>();
    private File fileToSend;
    private LogsListAdapter logsFileAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_logs);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Saved Data");
        }
        resultTextView= findViewById(R.id.resultTextView);
        logsFileListView= findViewById(R.id.logsFileListView);
        logsFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show dialog for open / send file
                final File clickedFile = logsFileList.get(position);

                new AlertDialog.Builder(ShareLogs.this)
                        .setTitle("Choose a file action")
                        .setItems(new CharSequence[]{"Open file", "Share logs"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                // Open File
                                                try {
                                                    Intent intent = new Intent();
                                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    Uri uri = FileProvider.getUriForFile(ShareLogs.this,
                                                            BuildConfig.APPLICATION_ID, clickedFile);
                                                    intent.setDataAndType(uri, getMimeType(clickedFile.getName()));
                                                    startActivity(intent);
                                                }catch (Exception ex){
                                                    Toast.makeText(getApplicationContext(), "Please have downloaded file editor, e.g. Excel", Toast.LENGTH_LONG).show();
                                                }
                                                break;
                                            case 1:
                                                fileToSend=clickedFile;
                                                //Send file by email
                                                Intent emailIntent  = new Intent(android.content.Intent.ACTION_SEND);
                                                emailIntent .setType("text/plain");
                                                //邮件接收者（数组，可以是多位接收者）
                                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"d.ke@student.fontys.nl"});
                                                String emailTitle = fileToSend.getName();
                                                String emailContent = "Send From Movesense";
                                                //Set email title
                                                emailIntent .putExtra(Intent.EXTRA_SUBJECT, emailTitle);
                                                //设置发送的内容 Set email content
                                                emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);
                                                Uri fileUri = FileProvider.getUriForFile(ShareLogs.this,
                                                        BuildConfig.APPLICATION_ID, clickedFile);
                                                //check file exists
                                                if (!fileToSend.exists() || !fileToSend.canRead()) {
                                                    return;
                                                }

                                                //附件 set attachment
                                                emailIntent .putExtra(Intent.EXTRA_STREAM, fileUri);
                                                //调用系统的邮件系统
                                                startActivity(Intent.createChooser(emailIntent , "Please choose"));
                                                break;
                                        }
                                    }
                                })
                        .show();
            }
        });

        // Create Adapter for listView
        logsFileAdapter = new LogsListAdapter(logsFileList);
        logsFileListView.setAdapter(logsFileAdapter);


        // Query logs from Movesense folder
        queryLogsFile();


    }

    private void queryLogsFile() {
        // Query logs from Movesense folder
        logsFileList.clear();

        File externalDirectory = Environment.getExternalStorageDirectory();
        File dirFile = new File(externalDirectory + "/Movesense");
        if (dirFile.exists()) {
            File[] logs = dirFile.listFiles();

            // Check if any file exists
            if (logs != null) {
                for (File file : logs) {
                    logsFileList.add(file);
                    Log.e(LOG_TAG, "Query File: " + file.getName());
                }

                logsFileAdapter.notifyDataSetChanged();
            } else {
                Log.e(LOG_TAG, "Query file failed. File[] = null");
                resultTextView.setText("Logs directory is empty or not loaded. Please subscribe sensor and back.");
            }
        } else {
            Log.e(LOG_TAG, "Movesense logs dir not exists.");
            resultTextView.setText("Logs directory not exists");
        }
    }


    private String getMimeType(String url) {
        String parts[] = url.split("\\.");
        String extension = parts[parts.length - 1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }



}