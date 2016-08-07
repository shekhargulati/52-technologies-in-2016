package playground

@Hash(algorithm = "MD5")
class Book {

    String title
    String author

    Book(String title, String author) {
        this.title = title
        this.author = author
    }

    @Override
    String toString() {
        return title + ", " + author
    }
}



