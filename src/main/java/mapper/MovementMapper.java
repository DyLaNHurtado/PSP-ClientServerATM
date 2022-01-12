package mapper;

import dto.MovementDTO;
import model.Movement;

public class MovementMapper extends BaseMapper<Movement, MovementDTO> {
    @Override
    public Movement fromDTO(MovementDTO item) {
        Movement movement = new Movement();
        if (item.getId() != null) {
            movement.setId(item.getId());
        }
        movement.setMoment(item.getMoment());
        movement.setType(item.getType());
        movement.setValue(item.getValue());
        movement.setUser(item.getUser());
        return movement;
    }

    @Override
    public MovementDTO toDTO(Movement item) {
        return MovementDTO.builder()
                .id(item.getId())
                .moment(item.getMoment())
                .type(item.getType())
                .value(item.getValue())
                .user(item.getUser())
                .build();
    }

}
