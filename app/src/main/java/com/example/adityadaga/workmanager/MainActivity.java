package com.example.adityadaga.workmanager;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button mbtnDownload, mbtnDownloadPreodic;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mbtnDownload = findViewById(R.id.btnDownload);
        mbtnDownloadPreodic = findViewById(R.id.btnDownloadPreodic);

        mbtnDownload.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnDownload:
                OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(CustomWorker.class).setInputData(CreateInputData()).build();
                WorkManager.getInstance().enqueue(simpleRequest);
                WorkManager.getInstance().getStatusById(simpleRequest.getId()).observe(this, new Observer<WorkStatus>() {
                    @Override
                    public void onChanged(@Nullable WorkStatus workStatus) {
                        switch (workStatus.getState()) {

                            case RUNNING:
                                mProgressDialog.setMessage("Downloading...");
                                mProgressDialog.show();
                                break;
                            case SUCCEEDED:
                                mProgressDialog.dismiss();
                                File file = new File(CustomWorker.saveFilePath);
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    getApplicationContext().startActivity(intent);
                                } catch (ActivityNotFoundException e) {

                                }
                                break;
                            case FAILED:
                                Toast.makeText(MainActivity.this, "Unable to Download", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                break;
                        }
                    }
                });
                break;
            case R.id.btnDownloadPreodic:
                PeriodicWorkRequest mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(CustomWorker.class, 2, TimeUnit.MINUTES).build();
                WorkManager.getInstance().enqueue(mPeriodicWorkRequest);
                WorkManager.getInstance().getStatusById(mPeriodicWorkRequest.getId()).observe(this, new Observer<WorkStatus>() {
                    @Override
                    public void onChanged(@Nullable WorkStatus workStatus) {
                        switch (workStatus.getState()) {

                            case RUNNING:
                                mProgressDialog.setMessage("Downloading...");
                                mProgressDialog.show();
                                break;
                            case SUCCEEDED:
                                mProgressDialog.dismiss();
                                File file = new File(CustomWorker.saveFilePath);
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    getApplicationContext().startActivity(intent);
                                } catch (ActivityNotFoundException e) {

                                }
                                break;
                            case FAILED:
                                Toast.makeText(MainActivity.this, "Unable to Download", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                break;

                        }
                    }
                });
                break;
        }

    }

    Data CreateInputData() {
        return new Data.Builder()
                .putString("Year", String.valueOf(2018))
                .putString("Month", String.valueOf(1)).
                        putString("VCNumber", "01517349464").
                        putString("SMSID", "17344166")
                .build();
    }


}
