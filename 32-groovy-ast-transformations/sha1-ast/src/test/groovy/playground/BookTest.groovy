package playground

import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class BookTest {

    @Test
    void 'should be able to set author and title of a book'() {

        def book = new Book("OpenShift Cookbook", "Shekhar Gulati")

        assertThat(book.getTitle(), equalTo("OpenShift Cookbook"))
        assertThat(book.getAuthor(), equalTo("Shekhar Gulati"))
    }


}
