package week2;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class Aggregation {

  static MongoCollection<Document> collection;

  public static void main(String[] args) {
    MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
    MongoDatabase db = mongoClient.getDatabase("school");
    collection = db.getCollection("students", Document.class);

    Block<Document> printBlock = new Block<Document>() {
      public void apply(final Document document) {
        System.out.println(document.toJson());
      }
    };

    collection.aggregate(Arrays.asList(
      unwind("$scores"),
      group(
        eq("_id", "$_id"),
        avg("average", "$scores.score")),
      sort(descending("average")),
      limit(1))).forEach(printBlock);

/*    db.students.aggregate( [
      { '$unwind': '$scores' },
    {
      '$group':
      {
        '_id': '$_id',
        'average': { $avg: '$scores.score' }
      }
    },
    { '$sort': { 'average' : -1 } },
    { '$limit': 1 } ] )*/

  }
}
