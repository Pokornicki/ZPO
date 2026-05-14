package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "student",
        indexes = {
                @Index(name = "idx_nazwisko", columnList = "nazwisko"),
                @Index(name = "idx_nr_indeksu", columnList = "nr_indeksu", unique = true)
        }
)
public class Student {

    @Id
    @GeneratedValue
    @Column(name = "student_id")
    private Integer studentId;

    @NotBlank(message = "Pole imie nie może być puste!")
    @Size(min = 2, max = 50, message = "Imię musi zawierać od {min} do {max} znaków!")
    @Column(nullable = false, length = 50)
    private String imie;

    @NotBlank(message = "Pole nazwisko nie może być puste!")
    @Size(min = 2, max = 50, message = "Nazwisko musi zawierać od {min} do {max} znaków!")
    @Column(nullable = false, length = 50)
    private String nazwisko;

    @NotBlank(message = "Pole nr indeksu nie może być puste!")
    @Size(min = 3, max = 20, message = "Numer indeksu musi zawierać od {min} do {max} znaków!")
    @Column(name = "nr_indeksu", nullable = false, unique = true, length = 20)
    private String nrIndeksu;

    @Email(message = "Niepoprawny format adresu e-mail!")
    @Column(length = 100)
    private String email;

    @ManyToMany(mappedBy = "studenci")
    @JsonIgnoreProperties({"studenci", "zadania"})
    private Set<Projekt> projekty;
}