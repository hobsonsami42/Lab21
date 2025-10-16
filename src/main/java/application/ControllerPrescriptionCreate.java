package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import application.model.*;
import application.service.*;
import view.*;

/*
 * Controller class for creating new prescriptions.
 */
@Controller
public class ControllerPrescriptionCreate {

    @Autowired
    PrescriptionRepository prescriptionRepository;

    @Autowired
    SequenceService sequence;

    /*
     * Request for new prescription form.
     */
    @GetMapping("/prescription/new")
    public String getNewPrescriptionForm(Model model) {
        model.addAttribute("prescription", new PrescriptionView());
        return "prescription_create";
    }

    /*
     * Process prescription creation.
     */
    @PostMapping("/prescription")
    public String createPrescription(PrescriptionView prescriptionView, Model model) {
        // get the next unique id for prescription
        int rxid = sequence.getNextSequence("PRESCRIPTION_SEQUENCE");

        // create a prescription model instance and copy data from view
        Prescription p = new Prescription();
        p.setRxid(rxid);
        p.setDrugName(prescriptionView.getDrugName());
        p.setQuantity(prescriptionView.getQuantity());
        p.setPatientId(prescriptionView.getPatientId());
        p.setDoctorId(prescriptionView.getDoctorId());
        p.setDateCreated(prescriptionView.getDateCreated());
        p.setRefills(prescriptionView.getRefills());
        prescriptionRepository.insert(p);

        // copy generated id back to view for display
        prescriptionView.setRxid(rxid);
        model.addAttribute("message", "Prescription created successfully.");
        model.addAttribute("prescription", prescriptionView);
        return "prescription_show";
    }
}