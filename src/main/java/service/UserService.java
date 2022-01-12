package service;

import dto.UserDTO;
import mapper.UserMapper;
import model.User;
import org.bson.types.ObjectId;
import repository.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserService extends BaseService<User, ObjectId, UserRepository> {

    UserMapper mapper = new UserMapper();

    //Dependency Injections. The constructor needs this repository
    public UserService(UserRepository repository) {
        super(repository);
    }


    public List<UserDTO> getAllUsers() throws SQLException {
        return mapper.toDTO(this.findAll());
    }

    public UserDTO getUserById(ObjectId id) throws SQLException {
        return mapper.toDTO(this.getById(id));
    }

    public UserDTO postUser(UserDTO userDTO) throws SQLException {
        User res = this.save(mapper.fromDTO(userDTO));
        return mapper.toDTO(res);
    }

    public UserDTO updateUser(UserDTO userDTO) throws SQLException {
        User res = this.update(mapper.fromDTO(userDTO));
        return mapper.toDTO(res);
    }

    public UserDTO deleteUser(UserDTO userDTO) throws SQLException {
        User res = this.delete(mapper.fromDTO(userDTO));
        return mapper.toDTO(res);
    }

}
