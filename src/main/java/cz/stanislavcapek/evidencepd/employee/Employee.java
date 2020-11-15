package cz.stanislavcapek.evidencepd.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * An instance of class {@code Employee}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Setter(value = AccessLevel.NONE)
    private int id;
    private String firstName;
    @EqualsAndHashCode.Exclude
    private String lastName;

    @JsonIgnore
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    @JsonIgnore
    public String getAbbreviation() {
        return (firstName.charAt(0) + lastName.substring(0, 2)).toUpperCase();
    }
}
