package week2;

import com.mongodb.HelloWorldSparkFreemarkerStyle;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.bson.Document;
import org.bson.conversions.Bson;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class HelloWorldAltogether {

  static MongoCollection<Document> collection;

  public static void main(String[] args) {
    final Configuration configuration = new Configuration();
    configuration.setClassForTemplateLoading(HelloWorldAltogether.class, "/");

    MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
    MongoDatabase db = mongoClient.getDatabase("week2Course");
    collection = db.getCollection("helloWorldSpark", Document.class);
    collection.drop();

    insertOneDocument();


    Spark.get(new Route("/") {
      @Override
      public Object handle(final Request request,
                           final Response response) {
        StringWriter writer = new StringWriter();
        try {
          Template helloTemplate = configuration.getTemplate("hello.ftl");
          Document titleDoc = collection.find().first();
          helloTemplate.process(titleDoc, writer);

        } catch (Exception e) {
          halt(500);
          e.printStackTrace();
        }
        return writer;
      }
    });
  }

  public static void insertOneDocument() {
    collection.insertOne(
      new Document("title", "Synch MongoDB & Spark")
    );
  }

}
