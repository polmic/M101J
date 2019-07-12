package course;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class BlogPostDAO {

  MongoCollection<Document> postsCollection;

  public BlogPostDAO(final MongoDatabase blogDatabase) {
    postsCollection = blogDatabase.getCollection("posts");
  }

  // Return a single post corresponding to a permalink
  public Document findByPermalink(String permalink) {
    return postsCollection.find(eq("permalink", permalink)).first();
  }

  // Return a list of posts in descending order. Limit determines
  // how many posts are returned.
  public List<Document> findByDateDescending(int limit) {
    return postsCollection
      .find()
      .sort(descending("date"))
      .limit(limit)
      .into(new ArrayList<Document>());
  }

  /*  {
        "_id" : ObjectId("513d396da0ee6e58987bae74"),
      "title" : "Martians to use MongoDB",
      "author" : "andrew",
      "body" : "Representatives from the planet Mars announced today that the planet would adopt MongoDB as a planetary standard. Head Martian Flipblip said that MongoDB was the perfect tool to store the diversity of life that exists on Mars.",
      "permalink" : "martians_to_use_mongodb",
      "tags" : [
        "martians",
          "seti",
          "nosql",
          "worlddomination"
    ],
        "comments" : [ ],
        "date" : ISODate("2013-03-11T01:54:53.692Z")
    }*/

  public String addPost(String title, String body, List tags, String username) {
    System.out.println("inserting blog entry " + title + " " + body);

    String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
    permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
    permalink = permalink.toLowerCase();

    Document post = new Document()
      .append("title", title)
      .append("author", username)
      .append("body", body)
      .append("permalink", permalink)
      .append("tags", tags)
      .append("comments", new ArrayList<Document>())
      .append("date", new Date(System.currentTimeMillis()));
      ;
    postsCollection.insertOne(post);
    return permalink;
  }


  // White space to protect the innocent

 /*   "comments" : [
  {
    "author" : "Larry Ellison",
    "body" : "While I am deeply disappointed that Mars won't be standardizing on a relational database, I understand their desire to adopt a more modern technology for the red planet.",
    "email" : "larry@oracle.com"
  },
  {
    "author" : "Salvatore Sanfilippo",
    "body" : "This make no sense to me. Redis would have worked fine."
  }
    ],*/
  // Append a comment to a blog post
  public void addPostComment(final String name, final String email, final String body,
                             final String permalink) {

    Document comment = new Document()
      .append("author", name)
      .append("body", body)
    ;
    if (email != null && !email.isEmpty())
      comment.append("email", email);

    postsCollection.updateOne(
      eq("permalink", permalink),
      addToSet("comments", comment),
      new UpdateOptions().upsert(true));

    

    // XXX HW 3.3, Work Here
    // Hints:
    // - email is optional and may come in NULL. Check for that.
    // - best solution uses an update command to the database and a suitable
    //   operator to append the comment on to any existing list of comments
  }
}
