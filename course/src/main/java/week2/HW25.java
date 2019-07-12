package week2;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Sorts.ascending;

public class HW25 {

  static MongoCollection<Document> collection;

  public static void main(String[] args) {
    MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
    MongoDatabase db = mongoClient.getDatabase("m101");
    collection = db.getCollection("movieDetails", Document.class);

    /* Which of the choices below is the title of a movie from the year 2013
    that is rated PG-13 and
    won no awards?
    Please query the video.movieDetails collection to find the answer. */
    //#########################
    //get2013PG13NoAwardsMovie();
    // ANSWER : l68(Below)

    //how many movies list "Sweden" second in the the list of countries.
    //#########################
    //getNumberOfMoviesWithSwedenSecond();
    // ANSWER : 6
  }

  public static void getNumberOfMoviesWithSwedenSecond() {
    Bson filter = eq("countries.1", "Sweden");
    List<Document> docsRetrieved = collection
      .find(filter)
      .into(new ArrayList<Document>());
    for (Document current : docsRetrieved)
      Course.printJson(current);
    System.out.println("\nCount : " + docsRetrieved.size());
  }

  public static void get2013PG13NoAwardsMovie() {
    Bson filter = eq("countries", "PG-13");
    //Bson sort = ascending("score");
    List<Document> docsRetrieved = collection
      .find(filter)
      //.sort(sort)
      .into(new ArrayList<Document>());
    for (Document current : docsRetrieved)
      Course.printJson(current);
    System.out.println("\nCount : " + docsRetrieved.size());
  }

}
/*

{
  "_id" : ObjectId("5692a3e124de1e0ce2dfda22"),
  "title" : "A Decade of Decadence, Pt. 2: Legacy of Dreams",
  "year" : 2013,
  "rated" : "PG-13",
  "released" : ISODate("2013-09-13T04:00:00.000Z"),
  "runtime" : 65,
  "countries" : ["USA"],
  "genres" : ["Documentary"],
  "director" : "Drew Glick",
  "writers" : ["Drew Glick"],
  "actors" : ["Gordon Auld", "Howie Boulware Jr.", "Tod Boulware", "Chen Drachman"],
  "plot" : "A behind the scenes look at the making of A Tiger in the Dark: The Decadence Saga.",
  "poster" : null,
  "imdb" : {
    "id" : "tt2199902",
    "rating" : 8,
    "votes" : 50
  },
  "awards" : {
    "wins" : 0,
    "nominations" : 0,
    "text" : ""
  },
  "type" : "movie"
} */
