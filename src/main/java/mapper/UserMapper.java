package mapper;

import dto.UserDTO;
import model.User;

public class UserMapper extends BaseMapper<User, UserDTO> {
    @Override
    public User fromDTO(UserDTO item) {
        User user = new User();
        if (item.getId() != null) {
            user.setId(item.getId());
        }
        user.setEmail(item.getEmail());
        user.setPin(item.getPin());
        user.setCash(item.getCash());
        return user;
    }

    @Override
    public UserDTO toDTO(User item) {
        return UserDTO.builder()
                .id(item.getId())
                .email(item.getEmail())
                .pin(item.getPin())
                .cash(item.getCash())
                .build();
    }

}
