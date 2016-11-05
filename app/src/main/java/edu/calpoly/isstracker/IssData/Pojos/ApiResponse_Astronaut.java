package edu.calpoly.isstracker.IssData.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApiResponse_Astronaut {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("people")
    @Expose
    private List<Astronaut> astronaut = new ArrayList<>();

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     *
     * @param number
     * The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     *
     * @return
     * The astronaut
     */
    public List<Astronaut> getAstronaut() {
        return astronaut;
    }

    /**
     *
     * @param astronaut
     * The people
     */
    public void setAstronaut(List<Astronaut> astronaut) {
        this.astronaut = astronaut;
    }
}
