package com.shekhargulati.peoplecounter;

import com.google.api.services.vision.v1.model.FaceAnnotation;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FaceDetectorTest {

//    @Test
    public void shouldReturnFaceAnnotationsAllThreePeople() throws Exception {
        Path image = Paths.get("src", "test", "resources", "random-people.png");
        FaceDetector faceDetector = new FaceDetector(GoogleVisionServiceFactory.getVisionServiceInstance("image-sentiment-analyzer"));
        List<FaceAnnotation> faceAnnotations = faceDetector.detectFaces(image);
        Path outputPath = ImageWriter.writeWithFaces(image, Paths.get("build"), faceAnnotations);
        System.out.println("Output file created at: " + outputPath.toAbsolutePath().toString());
        assertThat(faceAnnotations.size(), equalTo(3));
    }

    @Test
    public void shouldReturnFaceAnnotationForManOnly() throws Exception {
        Path image = Paths.get("src", "test", "resources", "monkey-and-man.jpg");
        FaceDetector faceDetector = new FaceDetector(GoogleVisionServiceFactory.getVisionServiceInstance("image-sentiment-analyzer"));
        List<FaceAnnotation> faceAnnotations = faceDetector.detectFaces(image);
        Path outputPath = ImageWriter.writeWithFaces(image, Paths.get("build"), faceAnnotations);
        System.out.println("Output file created at: " + outputPath.toAbsolutePath().toString());
        assertThat(faceAnnotations.size(), equalTo(1));
    }
}