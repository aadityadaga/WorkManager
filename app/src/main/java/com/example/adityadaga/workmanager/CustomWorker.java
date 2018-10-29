package com.example.adityadaga.workmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.work.Worker;

public class CustomWorker extends Worker {

    private String saveFilePath;
    private WorkerResult mWorkerresult;


    public CustomWorker() {
    }

    @NonNull
    @Override
    public WorkerResult doWork() {

        try {
       mWorkerresult =   downloadFile("https://www.random.in/Pages/DIY/Download-Soa-Pdf.aspx?year=2018&month=01&smsid=17344166&vcnumber=01517349464","" + getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mWorkerresult == WorkerResult.SUCCESS){
            File file = new File(saveFilePath);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent intent = Intent.createChooser(target, "Open File");
            try {
                getApplicationContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        }

        return WorkerResult.SUCCESS;
    }



    private WorkerResult downloadFile(String fileURL, String saveDir) throws IOException {
        final int BUFFER_SIZE = 4096;
        URL url = new URL(fileURL);

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
            InputStream inputStream = httpConn.getInputStream();
            saveFilePath = saveDir + File.separator + disposition;
            System.out.print("File Path = " + saveFilePath);
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            System.out.println("File downloaded");
            httpConn.disconnect();
            return WorkerResult.SUCCESS;
        } else {
            httpConn.disconnect();
            Toast.makeText(getApplicationContext(), "File Not Available ", Toast.LENGTH_SHORT).show();
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            return WorkerResult.FAILURE;
        }


    }

}
