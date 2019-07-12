package week2;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;

public class HW22 {

  static MongoCollection<Document> collection;

  public static void main(String[] args) {
    MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
    MongoDatabase db = mongoClient.getDatabase("students");
    collection = db.getCollection("grades", Document.class);

    //Find all exam scores greater than or equal to 65, and sort those scores from lowest to highest.
    //What is the student_id of the lowest exam score above 65?
    //#########################
    //getLowestScoreSudentID();
    //#########################
    //Answer : _id = 22

    //Remove the grade of type "homework" with the lowest score for each  student from the dataset
    /* If you select homework grade-documents, sort by student and then by score,
    you can iterate through and find the lowest score for each student by noticing a change in student id.
    As you notice that change of student_id, remove the document. */
    //#########################
    removeLowestGradeHomeworks();

    //#########################

  }

  public static void removeLowestGradeHomeworks() {
    Bson filter = gte("type", "homework");
    Bson sort = ascending("student_id", "score");
    List<Document> docsRetrieved = collection
      .find(filter)
      .sort(sort)
      .into(new ArrayList<Document>());
    System.out.println("\nCount : " + collection.count());
    LinkedList<Document> notes = new LinkedList<Document>();
    for (int i = 0; i < docsRetrieved.size(); i++) {
      Document current = docsRetrieved.get(i);
      Course.printJson(current);
      notes.add(current);
      int studentID = (Integer) current.get("student_id");
      if (i < docsRetrieved.size() -1) {
        int nextStudentID = (Integer) docsRetrieved.get(i + 1).get("student_id");
        System.out.println("current : " + studentID + " || next : " + nextStudentID);
        if (studentID != nextStudentID) {
          System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
          System.out.println("Document qui doit être supprimé : ");
          Course.printJson(notes.get(0));
          collection.deleteOne(notes.get(0));
          System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
          notes.clear();
        }
      }
    }
    List<Document> docsAfterRemoval = collection
      .find(filter)
      .sort(sort)
      .into(new ArrayList<Document>());
    for (Document doc : docsAfterRemoval) {
      Course.printJson(doc);
    }
    System.out.println("\nCount : " + collection.count());
  }

  public static void getLowestScoreSudentID() {
    Bson filter = gte("score", 65);
    Bson sort = ascending("score");
    List<Document> docsRetrieved = collection
      .find(filter)
      .sort(sort)
      .into(new ArrayList<Document>());
    for (Document current : docsRetrieved)
      Course.printJson(current);
    System.out.println("\nCount : " + collection.count());
  }
}
