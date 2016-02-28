package com.shekhargulati.peoplecounter;

import rx.Observable;
import twitter4j.MediaEntity;
import twitter4j.Status;

import java.util.concurrent.TimeUnit;

public class PeopleCounterApp {

    public static void main(String[] args) throws Exception {
        PeopleCounter peopleCounter = new PeopleCounter(GoogleVisionServiceFactory.getVisionServiceInstance("image-sentiment-analyzer"));

        Observable<Status> tweets = TweetObservable.of("Wenger");

        Observable<String> imageStream = tweets
                .filter(status -> status.getExtendedMediaEntities().length > 0)
                .flatMap(s -> Observable.from(s.getExtendedMediaEntities()))
                .map(MediaEntity::getMediaURL);

        imageStream
                .map(image -> peopleCounter.count(image))
                .subscribe(System.out::println, e -> e.printStackTrace());

//        Observable<AnnotateImageResponse> annotateImageResponseObservable = imageStream.buffer(5).doOnNext(System.out::println).flatMap(images -> Observable.from(peopleCounter.annotateImageUrls(images)));
//        annotateImageResponseObservable.forEach(r -> r.getFaceAnnotations().forEach(System.out::println));
    }

}
