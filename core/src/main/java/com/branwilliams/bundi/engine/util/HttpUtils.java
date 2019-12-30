package com.branwilliams.bundi.engine.util;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * @author Brandon
 * @since May 19, 2019
 */
public enum HttpUtils {
    INSTANCE;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";

    /**
     * Downloads the image at the provided URL to the file name in the directory provided.
     * @param directory The directory for the file named 'fileName'.
     * @param fileName The name of the file to download the image as, excluding extension.
     * @param url The direct URl to the image.
     * @return The url to the downloaded image. Null if unable to download.
     * */
    public static String downloadImage(Path directory, String fileName, String url, boolean addExtension) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            if (url.startsWith("https")) {
                connection = (HttpsURLConnection) new URL(url).openConnection();
            }
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            if (connection.getResponseCode() < 200 || connection.getResponseCode() > 299) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                System.err.println("Error code: " + connection.getResponseCode());
                br.lines().forEach(System.err::println);
                connection.disconnect();
                System.exit(1);
            }

            if (addExtension) {
                String contentType = connection.getHeaderField("Content-Type");
                String extension = contentType.substring(6 /*image/ == 6*/);
                fileName = fileName + "." + extension;
            }

            File outputFile = new File(directory.toFile(), fileName);
            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[2048];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return outputFile.getAbsolutePath();
        } catch (IOException | UncheckedIOException e) {
            System.err.println("Unable to parse url: " + url + ", e=" + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * Performs a GET request to the provided URL and consumes the webpage with the Jsoup parser.
     * */
    public static void get(String url, Consumer<String> consumer) {
        request("GET", url, consumer);
    }

    public static void request(String method, String url, Consumer<String> consumer) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            if (url.startsWith("https")) {
                connection = (HttpsURLConnection) new URL(url).openConnection();
            }

            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            connection.setRequestMethod(method);
            connection.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader br;
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                System.err.println("Error code: " + connection.getResponseCode());
                br.lines().forEach(System.err::println);
                connection.disconnect();
                System.exit(1);
            }

            StringBuilder stringBuilder = new StringBuilder();
            br.lines().forEach((l) -> stringBuilder.append(l).append("\n"));

            consumer.accept(stringBuilder.toString());

        } catch (IOException | UncheckedIOException e) {
            System.err.println("Unable to parse url: " + url + ", e=" + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
