package com.shekhargulati.peoplecounter;

import java.util.Objects;

public class ImagePeopleCount {

    private final String image;
    private final int count;

    public ImagePeopleCount(String image, int count) {
        this.image = image;
        this.count = count;
    }

    public String getImage() {
        return image;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImagePeopleCount that = (ImagePeopleCount) o;
        return count == that.count &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, count);
    }

    @Override
    public String toString() {
        return "ImagePeopleCount{" +
                "image='" + image + '\'' +
                ", count=" + count +
                '}';
    }
}
