package com.ghifar;

import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        while (true){
            try {
                downloadFileWithResume("http://uryourl.com", "okede.exe");
                break;
            } catch (SocketTimeoutException ex){
                ex.printStackTrace();
            }catch (RuntimeException ex){
                Thread.sleep(1000);
                ex.printStackTrace();
            }
        }

        System.out.println("complete");
    }

    private static long transferDataAndGetBytesDownloaded(URLConnection downloadFileConnection, File outputFile) throws IOException {
        System.out.println("Download started");

        long bytesDownloaded = 0;
        InputStream is =null;
        OutputStream os =null ;
        try {
            is = downloadFileConnection.getInputStream();
            os = new FileOutputStream(outputFile, true);

            byte[] buffer = new byte[1024];

            int bytesCount;
            while ((bytesCount = is.read(buffer)) > 0) {
                os.write(buffer, 0, bytesCount);
                bytesDownloaded += bytesCount;
            }
        }finally {
            if (is != null) is.close();
            if (os != null)os.close();
        }
        return bytesDownloaded;
    }

    public static long downloadFileWithResume(String downloadUrl, String saveAsFileName) throws IOException, URISyntaxException {
        File outputFile = new File(saveAsFileName);

        URLConnection downloadFileConnection = addFileResumeFunctionality(downloadUrl, outputFile);
        downloadFileConnection.setConnectTimeout(1000);
        downloadFileConnection.setReadTimeout(10000);
        return transferDataAndGetBytesDownloaded(downloadFileConnection, outputFile);
    }

    private static URLConnection addFileResumeFunctionality(String downloadUrl, File outputFile) throws IOException, URISyntaxException, ProtocolException, ProtocolException {
        long existingFileSize = 0L;
        URLConnection downloadFileConnection = new URI(downloadUrl).toURL()
            .openConnection();

        if (outputFile.exists() && downloadFileConnection instanceof HttpURLConnection) {
            HttpURLConnection httpFileConnection = (HttpURLConnection) downloadFileConnection;

            HttpURLConnection tmpFileConn = (HttpURLConnection) new URI(downloadUrl).toURL()
                .openConnection();
            tmpFileConn.setRequestMethod("HEAD");
            long fileLength = tmpFileConn.getContentLengthLong();
            existingFileSize = outputFile.length();

            System.out.println("Existing file size "+existingFileSize);
            System.out.println("actual file lengt "+fileLength);

            if (fileLength == -1) throw new RuntimeException("couldnt get through the URL ");

            if (existingFileSize < fileLength) {
                httpFileConnection.setRequestProperty("Range", "bytes=" + existingFileSize + "-" + fileLength);
            } else {
                throw new IOException("File Download already completed.");
            }
        }
        return downloadFileConnection;
    }

}
