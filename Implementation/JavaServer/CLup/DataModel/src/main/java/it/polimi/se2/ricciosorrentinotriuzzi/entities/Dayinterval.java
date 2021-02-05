package it.polimi.se2.ricciosorrentinotriuzzi.entities;

import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Entity
@Table(name = "dayinterval")

public class Dayinterval implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int dayOfTheWeek;
    private Time start;
    private Time end;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }


    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }
    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }


    public Time getStart() {
        return start;
    }
    public void setStart(Time start) {
        this.start = start;
    }


    public Time getEnd() {
        return end;
    }
    public void setEnd(Time end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "{id:" + id +", dayOfTheWeek:" + dayOfTheWeek + ", start:" + start + ", end:" + end + "}";
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("dayOfTheWeek", getDayOfTheWeek());
        json.put("start", getStart());
        json.put("end", getEnd());
        return json;
    }
}
