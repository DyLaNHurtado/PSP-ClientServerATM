package repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import database.MongoDBController;
import model.Movement;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MovementRepository implements CrudRepository<Movement, ObjectId> {

    @Override
    public List<Movement> findAll() {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<Movement> movementsCollection = mongoController.getCollection("ATM", "movements", Movement.class);
        List<Movement> list = movementsCollection.find().into(new ArrayList<>());
        mongoController.close();
        return list;
    }

    @Override
    public Movement getById(ObjectId objectId) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<Movement> movementsCollection = mongoController.getCollection("ATM", "movements", Movement.class);
        Movement movement = movementsCollection.find(eq("_id", objectId)).first();
        mongoController.close();
        if (movement != null)
            return movement;
        throw new SQLException("Error MovementsRepository dont exist movements with ID: " + objectId);
    }

    @Override
    public Movement save(Movement movement) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<Movement> movementsCollection = mongoController.getCollection("ATM", "movements", Movement.class);
        try {
            movementsCollection.insertOne(movement);
            return movement;
        } catch (Exception e) {
            throw new SQLException("Error MovementsRepository to movements movements into database: " + e.getMessage());
        } finally {
            mongoController.close();
        }
    }


    @Override
    public Movement update(Movement movement) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<Movement> movementsCollection = mongoController.getCollection("ATM", "movements", Movement.class);
        try {
            Document filtered = new Document("_id", movement.getId());
            FindOneAndReplaceOptions returnDoc = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
            return movementsCollection.findOneAndReplace(filtered, movement, returnDoc);
        } catch (Exception e) {
            throw new SQLException("Error MovementsRepository to update movements with id: " + movement.getId() + " " + e.getMessage());
        } finally {
            mongoController.close();
        }
    }

    @Override
    public Movement delete(Movement movement) throws SQLException {
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();
        MongoCollection<Movement> movementsCollection = mongoController.getCollection("ATM", "movements", Movement.class);
        try {
            Document filtered = new Document("_id", movement.getId());
            return movementsCollection.findOneAndDelete(filtered);
        } catch (Exception e) {
            throw new SQLException("Error MovementsRepository to delete movements with id: " + movement.getId() + " " + e.getMessage());
        } finally {
            mongoController.close();
        }
    }
}
