package cz.stanislavcapek.evidencepd.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An instance of class {@code Employee}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    private final int id;
    private String firstName;
    private String lastName;

    public Employee(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonIgnore
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    @JsonIgnore
    public String getAbbreviation() {
        return (firstName.charAt(0) + lastName.substring(0, 2)).toUpperCase();
    }
}
