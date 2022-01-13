package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.MovementDTO;
import org.bson.types.ObjectId;
import repository.MovementRepository;
import service.MovementService;

import java.sql.SQLException;
import java.util.List;

public class MovementController {

    private static MovementController controller = null;

    // Service
    private final MovementService movementService;

    // Singleton
    private MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    public static MovementController getInstance() {
        if (controller == null) {
            controller = new MovementController(new MovementService(new MovementRepository()));
        }
        return controller;
    }


    // I have used DTO in case I want to transport the info
    // Example
    // public List<MovementDTO> getAllMovements() {
    //    return MovementService.getAllMovements();
    //}

    public List<MovementDTO> getAllMovements() {
        try {
            return movementService.getAllMovements();
        } catch (SQLException e) {
            System.err.println("Error MovementsController en getAllMovements: " + e.getMessage());
            return null;
        }
    }

    public MovementDTO getMovementById(ObjectId id) {
        try{
            return movementService.getMovementById(id);
        } catch (SQLException e) {
            System.err.println("Error MovementsController en getMovementById: " + e.getMessage());
            return null;
        }
    }

    public MovementDTO postMovement(MovementDTO movementDTO) {
        try {
            return movementService.postMovement(movementDTO);
        } catch (SQLException e) {
            System.err.println("Error MovementsController en postMovement: " + e.getMessage());
            return null;
        }
    }

    public MovementDTO updateMovement(MovementDTO movementDTO) {
        try {
            return movementService.updateMovement(movementDTO);
        } catch (SQLException e) {
            System.err.println("Error MovementsController en updateMovement: " + e.getMessage());
            return null;
        }
    }

    public MovementDTO deleteMovement(MovementDTO movementDTO) {
        try {
            return movementService.deleteMovement(movementDTO);
        } catch (SQLException e) {
            System.err.println("Error MovementsController en deleteMovement: " + e.getMessage());
            return null;
        }
    }

    public String getAllMovementsJSON() {
        try {
            final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            return prettyGson.toJson(movementService.getAllMovements().stream().limit(25));
        } catch (SQLException e) {
            System.err.println("Error MovementsController en getAllMovementsJSON: " + e.getMessage());
            return "error";
        }
    }

}
