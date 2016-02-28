package com.shekhargulati.peoplecounter;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.FaceAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class PeopleCounter {

    private final FaceDetector faceDetector;

    public PeopleCounter(Vision vision) {
        this.faceDetector = new FaceDetector(vision);
    }

    public ImagePeopleCount count(String imageUrl) {
        try {
            List<FaceAnnotation> faceAnnotations = faceDetector.detectFaces(urlToByteArray(imageUrl));
            if (faceAnnotations == null || faceAnnotations.isEmpty()) {
                return new ImagePeopleCount(imageUrl, 0);
            }
            Path outputPath = ImageWriter.writeWithFaces1(imageUrl, Paths.get("build"), faceAnnotations);
            System.out.println("Output file created at: " + outputPath.toAbsolutePath().toString());
            return new ImagePeopleCount(imageUrl, faceAnnotations.size());
        } catch (IOException e) {
            return new ImagePeopleCount(imageUrl, 0);
        }
    }

    public ImagePeopleCount count(Path image) throws IOException {
        List<FaceAnnotation> faceAnnotations = faceDetector.detectFaces(Files.readAllBytes(image));
        if (faceAnnotations.isEmpty()) {
            return new ImagePeopleCount(image.toFile().getName(), 0);
        }
        return new ImagePeopleCount(image.toFile().getName(), faceAnnotations.size());
    }


    private byte[] urlToByteArray(String urlOfImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = new URL(urlOfImage).openStream()) {
            byte[] byteChunk = new byte[4096];
            int n;

            while ((n = inputStream.read(byteChunk)) > 0) {
                byteArrayOutputStream.write(byteChunk, 0, n);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", urlOfImage, e.getMessage());
            return new byte[0];
        }
    }

}