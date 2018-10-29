package com.example.adityadaga.workmanager;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

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
        switch (v.getId()){

            case R.id.btnDownload:
                OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(CustomWorker.class).build();
                WorkManager.getInstance().enqueue(simpleRequest);
                WorkManager.getInstance().getStatusById(simpleRequest.getId()).observe(this, new Observer<WorkStatus>() {
                    @Override
                    public void onChanged(@Nullable WorkStatus workStatus) {
                        switch (workStatus.getState()){

                            case RUNNING:
                                mProgressDialog.setMessage("Downloading...");
                                mProgressDialog.show();
                                break;
                            case SUCCEEDED:
                                mProgressDialog.dismiss();
                                break;
                            case FAILED:
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
                                break;
                            case FAILED:
                                mProgressDialog.dismiss();
                                break;
                        }
                    }
                });
                break;
        }




    }


}
