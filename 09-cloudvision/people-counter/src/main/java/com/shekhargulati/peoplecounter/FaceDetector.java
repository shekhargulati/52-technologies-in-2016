package com.shekhargulati.peoplecounter;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FaceDetector {

    private final Vision vision;

    public FaceDetector(Vision vision) {
        this.vision = vision;
    }

    public List<FaceAnnotation> detectFaces(Path image) throws IOException {
        return detectFaces(Files.readAllBytes(image));
    }

    public List<FaceAnnotation> detectFaces(byte[] image) throws IOException {
        BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest()
                .setRequests(
                        Collections.singletonList(
                                new AnnotateImageRequest()
                                        .setImage(new Image().encodeContent(image))
                                        .setFeatures(Collections.singletonList(new Feature().setType("FACE_DETECTION").setMaxResults(10)))
                        ));
        Vision.Images.Annotate annotate = vision.images().annotate(batchRequest);
        annotate.setDisableGZipContent(true);
        BatchAnnotateImagesResponse batchAnnotateImagesResponse = annotate.execute();
        if (batchAnnotateImagesResponse.getResponses().isEmpty()) {
            return Collections.emptyList();
        }
        AnnotateImageResponse annotateImageResponse = batchAnnotateImagesResponse.getResponses().get(0);
        return annotateImageResponse.getFaceAnnotations();
    }
}
