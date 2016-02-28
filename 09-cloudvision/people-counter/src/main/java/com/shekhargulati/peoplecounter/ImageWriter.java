package com.shekhargulati.peoplecounter;

import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Vertex;
import com.google.common.io.Files;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ImageWriter {

    public static Path writeWithFaces1(String urlOfImage, Path outputPath, List<FaceAnnotation> faces) throws IOException {
        URL url = new URL(urlOfImage);
        BufferedImage img = ImageIO.read(url.openStream());
        annotateWithFaces(img, faces);
        File output = outputPath.resolve(UUID.randomUUID().toString()).toFile();
        ImageIO.write(img, "jpg", output);
        return output.toPath();
    }

    public static Path writeWithFaces(Path inputPath, Path outputPath, List<FaceAnnotation> faces) throws IOException {
        BufferedImage img = ImageIO.read(inputPath.toFile());
        annotateWithFaces(img, faces);
        File output = outputPath.resolve(inputPath.getFileName()).toFile();
        ImageIO.write(img, Files.getFileExtension(inputPath.toAbsolutePath().toString()), output);
        return output.toPath();
    }

    private static void annotateWithFaces(BufferedImage img, List<FaceAnnotation> faces) {
        for (FaceAnnotation face : faces) {
            annotateWithFace(img, face);
        }
    }

    private static void annotateWithFace(BufferedImage img, FaceAnnotation face) {
        Graphics2D gfx = img.createGraphics();
        Polygon poly = new Polygon();
        for (Vertex vertex : face.getFdBoundingPoly().getVertices()) {
            poly.addPoint(vertex.getX(), vertex.getY());
        }
        gfx.setStroke(new BasicStroke(10));
        List<String> likelihoods = Arrays.asList("LIKELY", "VERY_LIKELY");
        if (likelihoods.contains(face.getJoyLikelihood())) {
            gfx.setColor(new Color(0x50FF1C));
        } else if (likelihoods.contains(face.getSorrowLikelihood())) {
            gfx.setColor(new Color(0xFF151F));
        } else if (likelihoods.contains(face.getSurpriseLikelihood())) {
            gfx.setColor(new Color(0xFFA631));
        } else {
            gfx.setColor(new Color(0xFFEE27));
        }
        gfx.draw(poly);
    }


}
