package com.project.controller;

import com.project.model.Projekt;
import com.project.service.ProjektService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class ProjektRestController {

    private static final Logger logger = LoggerFactory.getLogger(ProjektRestController.class);

    private final ProjektService projektService;

    public ProjektRestController(ProjektService projektService) {
        this.projektService = projektService;
    }

    @GetMapping("/projekty/{projektId}")
    public ResponseEntity<Projekt> getProjekt(@PathVariable("projektId") Integer projektId) {
        return ResponseEntity.of(projektService.getProjekt(projektId));
    }

    @PostMapping("/projekty")
    public ResponseEntity<Void> createProjekt(@Valid @RequestBody Projekt projekt) {
        Projekt createdProjekt = projektService.setProjekt(projekt);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{projektId}")
                .buildAndExpand(createdProjekt.getProjektId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/projekty/{projektId}")
    public ResponseEntity<Void> updateProjekt(
            @Valid @RequestBody Projekt projekt,
            @PathVariable("projektId") Integer projektId) {

        return projektService.getProjekt(projektId)
                .map(p -> {
                    projekt.setProjektId(projektId);
                    projektService.setProjekt(projekt);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/projekty/{projektId}")
    public ResponseEntity<Void> deleteProjekt(@PathVariable("projektId") Integer projektId) {
        return projektService.getProjekt(projektId)
                .map(p -> {
                    projektService.deleteProjekt(projektId);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/projekty")
    public Page<Projekt> getProjekty(Pageable pageable) {
        logger.info("Pobieranie listy projektów");
        return projektService.getProjekty(pageable);
    }

    @GetMapping(value = "/projekty", params = "nazwa")
    public Page<Projekt> getProjektyByNazwa(
            @RequestParam(name = "nazwa") String nazwa,
            Pageable pageable) {

        return projektService.searchByNazwa(nazwa, pageable);
    }
}