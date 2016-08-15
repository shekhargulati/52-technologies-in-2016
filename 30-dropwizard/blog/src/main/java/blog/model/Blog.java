package blog.model;

import java.util.Date;
import java.util.UUID;
 
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
 
public class Blog {
 
    private String id = UUID.randomUUID().toString();

    private String _id;
 
    @NotBlank
    private String title;
 
    @URL
    @NotBlank
    private String url;
 
    private final Date publishedOn = new Date();
 
    public Blog() {
    }
 
    public Blog(String title, String url) {
        super();
        this.title = title;
        this.url = url;
    }
 
    public String getId() {
        return id;
    }
 
    public String getTitle() {
        return title;
    }
 
    public String getUrl() {
        return url;
    }
 
    public Date getPublishedOn() {
        return publishedOn;
    }

    public String get_id() {
        return _id;
    }
}