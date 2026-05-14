package com.project.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.Set;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projekt")
public class Projekt {

    @Id
    @GeneratedValue
    @Column(name = "projekt_id")
    private Integer projektId;

    @NotBlank(message = "Pole nazwa nie może być puste!")
    @Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!")
    @Column(nullable = false, length = 50)
    private String nazwa;

    @Size(max = 1000, message = "Opis może mieć maksymalnie {max} znaków!")
    @Column(length = 1000)
    private String opis;

    @Column(name = "data_oddania")
    private LocalDate dataOddania;

    @OneToMany(mappedBy = "projekt")
    @JsonIgnoreProperties({"projekt"})
    private List<Zadanie> zadania;

    @ManyToMany
    @JoinTable(
            name = "projekt_student",
            joinColumns = {@JoinColumn(name = "projekt_id")},
            inverseJoinColumns = {@JoinColumn(name = "student_id")}
    )
    @JsonIgnoreProperties({"projekty"})
    private Set<Student> studenci;
}