package controller;

import dto.UserDTO;
import model.User;
import org.bson.types.ObjectId;
import repository.UserRepository;
import service.UserService;

import java.sql.SQLException;
import java.util.List;

public class UserController {
    private static UserController controller = null;

    // Service
    private final UserService userService;

    // Singleton
    private UserController(UserService userService) {
        this.userService = userService;
    }

    public static UserController getInstance() {
        if (controller == null) {
            controller = new UserController(new UserService(new UserRepository()));
        }
        return controller;
    }


    // I have used DTO in case I want to transport the info
    // Example
    //public List<UserDTO> getAllUsers() {
    //    return UserService.getAllUsers();
    //}

    public List<UserDTO> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (SQLException e) {
            System.err.println("Error UserController en getAllUsers: " + e.getMessage());
            return null;
        }
    }

    public UserDTO getUserById(ObjectId id) {
        try{
            return userService.getUserById(id);
        } catch (SQLException e) {
            System.err.println("Error UserController en getUserById: " + e.getMessage());
            return null;
        }
    }

    public UserDTO postUser(UserDTO userDTO) {
        try {
            return userService.postUser(userDTO);
        } catch (SQLException e) {
            System.err.println("Error UserController en postUser: " + e.getMessage());
            return null;
        }
    }

    public UserDTO updateUser(UserDTO userDTO) {
        try {
            return userService.updateUser(userDTO);
        } catch (SQLException e) {
            System.err.println("Error UserController en updateUser: " + e.getMessage());
            return null;
        }
    }

    public UserDTO deleteUser(UserDTO userDTO) {
        try {
            return userService.deleteUser(userDTO);
        } catch (SQLException e) {
            System.err.println("Error UserController en deleteUser: " + e.getMessage());
            return null;
        }
    }

}
