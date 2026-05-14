package com.project.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "zadanie")
public class Zadanie {

    @Id
    @GeneratedValue
    @Column(name = "zadanie_id")
    private Integer zadanieId;

    @NotBlank(message = "Pole nazwa nie może być puste!")
    @Size(min = 3, max = 100, message = "Nazwa musi zawierać od {min} do {max} znaków!")
    @Column(nullable = false, length = 100)
    private String nazwa;

    @Size(max = 1000, message = "Opis może mieć maksymalnie {max} znaków!")
    @Column(length = 1000)
    private String opis;

    private Integer kolejnosc;

    @Column(name = "dataczas_dodania")
    private LocalDateTime dataczasDodania = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "projekt_id")
    @JsonIgnoreProperties({"zadania", "studenci"})
    private Projekt projekt;
}