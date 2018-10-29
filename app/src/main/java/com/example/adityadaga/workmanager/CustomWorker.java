package com.example.adityadaga.workmanager;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.work.Worker;

public class CustomWorker extends Worker {

    public static String saveFilePath;

    private WorkerResult mWorkerresult;
    private String Year;
    private String Month;
    private String SMSID;
    private String VCNumber;


    public CustomWorker() {
    }

    @NonNull
    @Override
    public WorkerResult doWork() {

        try {
             Year = getInputData().getString("Year", "");
             Month = getInputData().getString("Month", "");
             SMSID = getInputData().getString("SMSID", "");
            VCNumber =getInputData().getString("VCNumber", "");
            System.out.println("https://www.random.in/Pages/DIY/Download-Soa-Pdf.aspx?year="+Year+"&month="+Month+"&smsid="+SMSID+"&vcnumber="+VCNumber);
            mWorkerresult = downloadFile("https://www.dishtv.in/Pages/DIY/Download-Soa-Pdf.aspx?year="+Year+"&month="+Month+"&smsid="+SMSID+"&vcnumber="+VCNumber, "" + getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mWorkerresult;

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
