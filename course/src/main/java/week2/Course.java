package week2;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;

public class Course {

  static MongoCollection<Document> collection;

  public static void main(String[] args) {
    MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
    MongoDatabase db = mongoClient.getDatabase("week2-blog");
    collection = db.getCollection("blog", Document.class);
    //collection.drop();

    //insertTwoDocumentsAsList();
    //insertTenDocumentsAndFind();
    //insertAndFindWithFilters();
    //queryWithProjection();
    //queryWithSort();
    //queryWithUpdate();
    deleteQuery();
  }

  private static void deleteQuery() {
    for (int i = 0; i < 8; i++)
      collection.insertOne(new Document()
        .append("_id", i)
      );

    collection.deleteOne(gt("_id", 2)); //will delete the first document with _id > 2
    collection.deleteOne(eq("_id", 4)); //will delete the first document with _id == 4
    collection.deleteMany(gt("_id", 6)); //will delete all documents with _id > 6

    for (Document current : collection.find().into(new ArrayList<Document>()))
      printJson(current);
  }

  private static void queryWithUpdate() {
    for (int i = 0; i < 8; i++)
      collection.insertOne(new Document()
        .append("_id", i)
        .append("x", i)
        .append("y", true)
      );

    // replaces the whole document, here y field will disapear.
    //collection.replaceOne(eq("x",6), new Document("x",20).append("updated", true));

    //with the update method, given fields will be replaced, but existing ones will remain
    /* collection.updateOne(
      eq("x", 6),
      new Document("$set",
        new Document("x", 18).append("updated", true))
    ); */

    //with one update operator
    //collection.updateOne(eq("x", 6), set("x", 20));

    //with multiple update operators using combine
    //collection.updateOne(eq("x", 6), combine(set("x", 20), set("updated", true)));

    //if the document matching update doesn't exist, it will be created by the update method with UpdateOptions().upsert(true)
    collection.updateOne(
      eq("_id", 10), combine(set("x", 99), set("updated", true)),
      new UpdateOptions().upsert(true));

    //updating multiple documents :
    collection.updateMany(
      gte("x", 5), inc("x", 1) //inc method will increment the value of x by 1
    );

    for (Document current : collection.find().into(new ArrayList<Document>()))
      printJson(current);
  }

  private static void queryWithSort() {
    for (int i = 0; i < 10; i++)
      for (int j = 0; j < 10; j++)
        collection.insertOne(new Document()
          .append("i", i)
          .append("j", j));

    Bson projection = fields(include("i", "j"), excludeId());
    //Bson sort = new Document("i", 1).append("j", -1); // 1 for ascending order & -1 for descending order
    //Bson sort = ascending("i", "j");
    //Bson sort = descending("i", "j");
    Bson sort = orderBy(ascending("i"), descending("j"));
    List<Document> docsFiltered = collection
      .find()
      .projection(projection)
      .sort(sort)
      .skip(20) //skips the first 20 results
      .limit(20) //get only the first 20 results
      .into(new ArrayList<Document>());
    for (Document current : docsFiltered)
      printJson(current);
    System.out.println("\nCount : " + collection.count());
  }

  public static void queryWithProjection() {
    for (int i = 0; i < 10; i++)
      collection.insertOne(new Document()
        .append("x", new Random().nextInt(2))
        .append("y", new Random().nextInt(100))
        .append("i", i));

    Bson filter = and(eq("x", 0), gt("y", 10), lt("y", 50));

    //Projection excludes fields with 0, here the x and _id fields will be excluded
    //Bson projection = new Document("x", 0).append("_id", 0);

    //Projection includes fields with 1, here the y and i fields will be included
    //Bson projection = new Document("y", 1).append("i", 1);

    Bson projection = fields(include("y", "i"), excludeId());

    List<Document> docsFiltered = collection
      .find(filter)
      .projection(projection)
      .into(new ArrayList<Document>());
    for (Document current : docsFiltered)
      printJson(current);
    System.out.println("\nCount : " + collection.count(filter));
  }

  public static void insertAndFindWithFilters() {
    for (int i = 0; i < 10; i++)
      collection.insertOne(new Document()
        .append("x", new Random().nextInt(2))
        .append("y", new Random().nextInt(100)));

    /*
    Bson filter = new Document()
      .append("x", 0)
      .append("y", new Document("$gt", 10).append("$lt", 50)
      );
    */

    Bson filter = and(eq("x", 0), gt("y", 10), lt("y", 50));

    List<Document> docsFiltered = collection.find(filter).into(new ArrayList<Document>());
    for (Document current : docsFiltered)
      printJson(current);

    System.out.println("\nCount : " + collection.count(filter));
  }

  public static void insertTenDocumentsAndFind() {
    for (int i = 0; i < 10; i++)
      collection.insertOne(new Document("x", i));

    System.out.println("\nFind one :");
    printJson(collection.find().first());

    System.out.println("\nFind all with into :");
    List<Document> allDocs = collection.find().into(new ArrayList<Document>());
    for (Document current : allDocs)
      printJson(current);

    System.out.println("\nFind all with iteration :");
    MongoCursor cursor = collection.find().iterator();
    try {
      while (cursor.hasNext())
        printJson((Document) cursor.next());
    } finally {
      cursor.close();
    }

    System.out.println("\nCount :");
    System.out.println(collection.count());
  }

  public static void insertTwoDocumentsAsList() {
    Document paul = new Document()
      .append("name", "Paul")
      .append("age", 26)
      .append("profession", "developper");
    Document john = new Document()
      .append("name", "John")
      .append("age", 31)
      .append("profession", "teacher");
    collection.insertMany(asList(paul, john));
  }

  public static void printJson(Document document) {
    JsonWriter jsonWriter = new JsonWriter(new StringWriter(), new JsonWriterSettings(JsonMode.SHELL, true));
    new DocumentCodec().encode(jsonWriter, document, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
    System.out.println(jsonWriter.getWriter());
    System.out.flush();
  }
}
