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

    public boolean indetificarUsuario(String email, String pin) {
        Utils utils = Utils.getInstance();
        if (utils.validateEmail(email)) {
            UserController userController = UserController.getInstance();
            userDTO = userController.getUserByEmail(email);
            if (userDTO != null) {
                return utils.toSHA512(userDTO.getPin()).equals(pin) && userDTO.getEmail().equals(email);
            } else {
                System.err.println("Could not identify this user !");
                return false;
            }
        } else {
            System.err.println("Could not identify this user !");
            return false;
        }
    }

    public UserDTO getUser() {
        if(userDTO!=null){return userDTO;}

        return UserDTO.builder().build();
}
}