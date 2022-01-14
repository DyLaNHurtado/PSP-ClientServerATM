package service;

import controller.UserController;
import dto.UserDTO;
import model.User;
import utils.Utils;

import java.sql.ResultSet;
import java.util.Objects;
import java.util.Optional;

public class AccessService {
    // Patron Singleton -> Unsa sola instancia
    private static AccessService instance;
    private UserDTO userDTO;
    private AccessService() {
        //this.comp = new Compartido();
    }

    public static AccessService getInstance() {
        if (instance == null) {
            instance = new AccessService();
        }
        return instance;
    }

    public UserDTO indetificarUsuario(String email, String pin) {
        Utils utils = Utils.getInstance();
        if (utils.validateEmail(email)) {
            UserController userController = UserController.getInstance();
            userDTO = userController.getUserByEmail(email);
            if (userDTO != null) {
                if (utils.toSHA512(userDTO.getPin()).equals(pin) && userDTO.getEmail().equals(email)) {
                    return userDTO;
                } else {
                    System.err.println("Could not identify this user !");
                    return null;
                }
            } else {
                System.err.println("Could not identify this user !");
                return null;
            }
        } else {
            return null;
        }
    }

    public UserDTO getUser() {
        if(userDTO!=null){return userDTO;}

        return UserDTO.builder().build();
}
}