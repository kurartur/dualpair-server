package com.artur.dualpair.server.domain.model.user;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Embeddable
public class AgeInfo implements Serializable {

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    private Integer age;

    private AgeInfo() {}

    public AgeInfo(Date dateOfBirth) {
        if (dateOfBirth == null) {
            this.dateOfBirth = null;
            this.age = null;
        } else {
            this.dateOfBirth = dateOfBirth;
            this.age = calculateAge(dateOfBirth);
        }
    }

    private Integer calculateAge(Date dateOfBirth) {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
            age--;
        }

        return age;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }
}
