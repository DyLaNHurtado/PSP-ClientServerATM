package repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import database.MongoDBController;
import model.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class UserRepository implements CrudRepository<User, ObjectId>{

    @Override
    public List<User> findAll() {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<User> userCollection = mongoController.getCollection("ATM", "users", User.class);
        List<User> list = userCollection.find().into(new ArrayList<>());
        mongoController.close();
        return list;
    }

    @Override
    public User getById(ObjectId objectId) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<User> userCollection = mongoController.getCollection("ATM", "users", User.class);
        User user = userCollection.find(eq("_id", objectId)).first();
        mongoController.close();
        if (user != null)
            return user;
        throw new SQLException("Error UserRepository dont exist user with ID: " + objectId);
    }

    @Override
    public User save(User user) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<User> userCollection = mongoController.getCollection("ATM", "users", User.class);
        try {
            userCollection.insertOne(user);
            return user;
        } catch (Exception e) {
            throw new SQLException("Error UserRepository to insert user into database: " + e.getMessage());
        } finally {
            mongoController.close();
        }
    }


    @Override
    public User update(User user) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<User> userCollection = mongoController.getCollection("ATM", "users", User.class);
        try {
            Document filtered = new Document("_id", user.getId());
            FindOneAndReplaceOptions returnDoc = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
            return userCollection.findOneAndReplace(filtered, user, returnDoc);
        } catch (Exception e) {
            throw new SQLException("Error UserRepository to update user with id: " + user.getId() + " " + e.getMessage());
        } finally {
            mongoController.close();
        }
    }

    @Override
    public User delete(User user) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<User> userCollection = mongoController.getCollection("ATM", "users", User.class);
        try {
            Document filtered = new Document("_id", user.getId());
            return userCollection.findOneAndDelete(filtered);
        } catch (Exception e) {
            throw new SQLException("Error UserRepository to delete user with id: " + user.getId() + " " + e.getMessage());
        } finally {
            mongoController.close();
        }
    }

    public User getByEmail(String email) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<User> userCollection = mongoController.getCollection("ATM", "users", User.class);
        User user = userCollection.find(eq("email", email)).first();
        mongoController.close();
        if (user != null)
            return user;
        throw new SQLException("Error UserRepository dont exist user with email: " + email);
    }
}
