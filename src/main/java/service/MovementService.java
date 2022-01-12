package service;

import dto.MovementDTO;
import mapper.MovementMapper;
import model.Movement;
import org.bson.types.ObjectId;
import repository.MovementRepository;

import java.sql.SQLException;
import java.util.List;

public class MovementService extends BaseService<Movement, ObjectId, MovementRepository> {

    MovementMapper mapper = new MovementMapper();

    //Dependency Injections. The constructor needs this repository
    public MovementService(MovementRepository repository) {
        super(repository);
    }


    public List<MovementDTO> getAllMovements() throws SQLException {
        return mapper.toDTO(this.findAll());
    }

    public MovementDTO getMovementById(ObjectId id) throws SQLException {
        return mapper.toDTO(this.getById(id));
    }

    public MovementDTO postMovement(MovementDTO movementDTO) throws SQLException {
        Movement res = this.save(mapper.fromDTO(movementDTO));
        return mapper.toDTO(res);
    }

    public MovementDTO updateMovement(MovementDTO movementDTO) throws SQLException {
        Movement res = this.update(mapper.fromDTO(movementDTO));
        return mapper.toDTO(res);
    }

    public MovementDTO deleteMovement(MovementDTO movementDTO) throws SQLException {
        Movement res = this.delete(mapper.fromDTO(movementDTO));
        return mapper.toDTO(res);
    }

}
