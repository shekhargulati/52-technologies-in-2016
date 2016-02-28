package com.shekhargulati.peoplecounter;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PeopleCounterTest {

    @Test
    public void shouldReturnPeopleCountInAnImage() throws Exception {
        Path image = Paths.get("src", "test", "resources", "random-people.png");
        PeopleCounter peopleCounter = new PeopleCounter(GoogleVisionServiceFactory.getVisionServiceInstance("image-sentiment-analyzer"));
        ImagePeopleCount imagePeopleCount = peopleCounter.count(image);
        assertThat(imagePeopleCount, equalTo(new ImagePeopleCount("random-people.png", 3)));
    }

    @Test
    public void shouldReturnOnlyPeopleCountInAnImage() throws Exception {
        Path image = Paths.get("src", "test", "resources", "monkey-and-man.jpg");
        PeopleCounter peopleCounter = new PeopleCounter(GoogleVisionServiceFactory.getVisionServiceInstance("image-sentiment-analyzer"));
        ImagePeopleCount imagePeopleCount = peopleCounter.count(image);
        assertThat(imagePeopleCount, equalTo(new ImagePeopleCount("monkey-and-man.jpg", 1)));
    }

}