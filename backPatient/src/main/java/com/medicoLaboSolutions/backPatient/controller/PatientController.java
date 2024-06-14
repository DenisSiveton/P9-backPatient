package com.medicoLaboSolutions.backPatient.controller;

import com.medicoLaboSolutions.backPatient.exceptions.PatientNotFoundException;
import com.medicoLaboSolutions.backPatient.model.dto.PatientDTO;
import com.medicoLaboSolutions.backPatient.model.pojo.Patient;
import com.medicoLaboSolutions.backPatient.service.PatientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(path = "/patients")
public class PatientController {

    private Logger logger = LoggerFactory.getLogger(PatientController.class);
    @Autowired
    private PatientService patientService;

    @GetMapping("/{lastname}")
    public ResponseEntity<Patient> getPatient(@PathVariable("lastname")  String lastname){
        logger.info("Retrieve patient info with the surname : {}", lastname);
        Patient patientFound = patientService.findPatientByLastname(lastname);
        return ResponseEntity.ok(patientFound);
    }
    @GetMapping("")
    public ResponseEntity<Iterable<Patient>> getAllPatients(){
        logger.info("Retrieve all patients info.");
        Iterable<Patient> listOfPatient = patientService.findAll();
        return ResponseEntity.ok(listOfPatient);
    }

    /**
     * This method checks if the data from the form are consistent and valid for a new BidList.
     * If the checks pass then add the new BidList to the database.
     *      It then retrieves all BidLists from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param patient Entity constructed from the form. It will be added to the database
     * @param result Form result. May contain errors if data don't comply
     * @return URI /patients. Show table with updated BidLists
     * @return In case of error : URI bidList/add. Returns to the form for a second attempt
     */
    @PostMapping(path = "")
    public ResponseEntity<Patient> addPatient(@Valid @RequestBody Patient patient, BindingResult result) {
        logger.info("Request : Add a new Patient");
        if (!result.hasErrors()) {
            Patient patientAdded = patientService.addNewPatient(patient);
            logger.info("{} {} wad added as a new patient.", patientAdded.getFirstname(), patientAdded.getLastname());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{lastname}")
                    .buildAndExpand(patientAdded.getLastname())
                    .toUri();

            return ResponseEntity.created(location).body(patientAdded);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") Integer id, @Valid @RequestBody PatientDTO patientWithUpdatedInfo, BindingResult result){
        logger.info("Request : Update patient with the id : {}",id);
        if(!result.hasErrors()){
            Patient patientUpdated =  patientService.updatePatient(patientWithUpdatedInfo, id);
            logger.info("Patient with the id : {} was updated",id);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{lastname}")
                    .buildAndExpand(patientUpdated.getLastname())
                    .toUri();

            return ResponseEntity.created(location).body(patientUpdated);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Patient> deletePatient(@PathVariable("id") Integer id){
        logger.info("Request : Delete patient with the id : {}",id);
        Patient patientDeleted = patientService.deleteById(id);
        logger.info("Patient with the id : {} was deleted",id);
        return ResponseEntity.ok(patientDeleted);
    }
}
