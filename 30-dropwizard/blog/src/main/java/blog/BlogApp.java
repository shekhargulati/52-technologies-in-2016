package blog;

import blog.model.Blog;
import blog.mongo.MongoHealthCheck;
import blog.mongo.MongoManaged;
import blog.resources.BlogResource;
import blog.resources.IndexResource;
import com.mongodb.DB;
import com.mongodb.Mongo;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.vz.mongodb.jackson.JacksonDBCollection;

public class BlogApp extends Application<AppConfiguration> {


    public static void main(String[] args) throws Exception {
        new BlogApp().run("server");
    }


    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
        Mongo mongo = new Mongo(configuration.mongohost, configuration.mongoport);
        MongoManaged mongoManaged = new MongoManaged(mongo);
        environment
                .lifecycle()
                .manage(mongoManaged);
        environment
                .healthChecks()
                .register("MongoHealthCheck", new MongoHealthCheck(mongo));

        DB db = mongo.getDB(configuration.mongodb);
        JacksonDBCollection<Blog, String> blogs = JacksonDBCollection.wrap(db.getCollection("blogs"), Blog.class, String.class);
        environment
                .jersey()
                .register(new IndexResource(blogs));
        environment
                .jersey()
                .register(new BlogResource(blogs));

    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {

    }
}
