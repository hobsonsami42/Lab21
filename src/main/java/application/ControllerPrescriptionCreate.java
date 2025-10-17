package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import application.model.*;
import application.service.*;
import view.*;

import java.util.List;

/*
 * Controller class for creating new prescriptions.
 */
@Controller
public class ControllerPrescriptionCreate {

    @Autowired
    PrescriptionRepository prescriptionRepository;

    @Autowired
    SequenceService sequence;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DrugRepository drugRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;

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
        Doctor doctor = doctorRepository.findById(prescriptionView.getDoctorId());
        if (doctor == null) {
            model.addAttribute("message", "Error: Doctor ID not found.");
            model.addAttribute("prescription", prescriptionView);
            return "prescription_create";
        }
        // Validate patient
        Patient patient = patientRepository.findById(prescriptionView.getPatientId());
        if (patient == null) {
            model.addAttribute("message", "Error: Patient ID not found.");
            model.addAttribute("prescription", prescriptionView);
            return "prescription_create";
        }
        // Validate drug
        Drug drug = drugRepository.findByName(prescriptionView.getDrugName());
        if (drug == null) {
            model.addAttribute("message", "Error: Drug name not found.");
            model.addAttribute("prescription", prescriptionView);
            return "prescription_create";
        }

        List<Pharmacy> pharmacies = pharmacyRepository.findByDrugCostsDrugName(drug.getName());
        Pharmacy pharmacy = (pharmacies != null && !pharmacies.isEmpty()) ? pharmacies.get(0) : null;


        Prescription p = new Prescription();
        p.setRxid(rxid);
        p.setDrugName(drug.getName());
        p.setQuantity(prescriptionView.getQuantity());
        p.setPatientId(patient.getId());
        p.setDoctorId(doctor.getId());
        p.setDateCreated(prescriptionView.getDateCreated());
        p.setRefills(prescriptionView.getRefills());
        p.setDateFilled(prescriptionView.getDateFilled());
        p.setFills(prescriptionView.getFills());

        if (pharmacy != null) {
            p.setPharmacyId(pharmacy.getId());
            p.setPharmacyName(pharmacy.getName());
            p.setPharmacyAddress(pharmacy.getAddress());
            p.setPharmacyPhone(pharmacy.getPhone());

            // Find the cost of this specific drug
            for (Pharmacy.DrugCost cost : pharmacy.getDrugCosts()) {
                if (cost.getDrugName().equalsIgnoreCase(drug.getName())) {
                    p.setCost(cost.getCost());
                    break;
                }
            }
        }

        prescriptionRepository.insert(p);


        prescriptionView.setRxid(rxid);
        prescriptionView.setDoctorFirstName(doctor.getFirstName());
        prescriptionView.setDoctorLastName(doctor.getLastName());
        prescriptionView.setPatientFirstName(patient.getFirstName());
        prescriptionView.setPatientLastName(patient.getLastName());

        if (pharmacy != null) {
            prescriptionView.setPharmacyID(pharmacy.getId());
            prescriptionView.setPharmacyName(pharmacy.getName());
            prescriptionView.setPharmacyAddress(pharmacy.getAddress());
            prescriptionView.setPharmacyPhone(pharmacy.getPhone());

            for (Pharmacy.DrugCost cost : pharmacy.getDrugCosts()) {
                if (cost.getDrugName().equalsIgnoreCase(drug.getName())) {
                    prescriptionView.setCost(String.valueOf(cost.getCost()));
                    break;
                }
            }
        }

        model.addAttribute("message", "Prescription created successfully.");
        model.addAttribute("prescription", prescriptionView);

        return "prescription_show";
    }
}
