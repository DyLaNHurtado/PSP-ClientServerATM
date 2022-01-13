package model;

import dto.UserDTO;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDate;

public class Movement {
    private ObjectId id;
    private Instant moment;
    private String type;
    private float value;
    private UserDTO userDTO;

    public Movement(){}
    public Movement(ObjectId id, Instant moment, String type, float value, UserDTO userDTO) {
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

}
