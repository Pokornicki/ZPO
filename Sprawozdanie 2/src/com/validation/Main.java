package com.validation;

import com.validation.exception.ValidationException;
import com.validation.validator.Validator;

public class Main {

    public static void main(String[] args) {
        try {
            Student student = new Student();

            student.setImie("Al");
            student.setNazwisko(null);
            student.setNrIndeksu("1234ABCD");
            student.setEmail("Grzegorz.Brzęczyszczykiewicz#pbs.edu.pl");

            Validator.validate(student);

            System.out.println("Walidacja zakończona sukcesem");

        } catch (ValidationException e) {
            System.out.println("Błędy walidacji:");
            System.out.println(e.getMessage());
        }
    }
}