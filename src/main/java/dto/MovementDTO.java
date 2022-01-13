package dto;

import lombok.Builder;
import model.User;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public class MovementDTO {
    private ObjectId id;
    private Instant moment;
    private String type;
    private float value;
    private UserDTO userDTO;

    public MovementDTO(ObjectId id, Instant moment, String type, float value, UserDTO userDTO) {
        this.id = id;
        this.moment = moment;
        this.type = type;
        this.value = value;
        this.userDTO = userDTO;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "_id=" + id +
                ", \"moment\"=" + moment +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", userDTO=" + userDTO +
                '}';
    }
}
