package com.ksug.lab;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;
    private String reservationName;

    Reservation() {}

    public Reservation(String reservationName) {
        this.reservationName = reservationName;
    }

    public Long getId() {
        return id;
    }

    public String getReservationName() {
        return reservationName;
    }

    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + id +
            ", reservationName='" + reservationName + '\'' +
            '}';
    }
}
