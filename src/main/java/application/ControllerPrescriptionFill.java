package application;

import application.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class ControllerPrescriptionFill {

  @Autowired
  private PrescriptionRepository prescriptionRepository;

  @Autowired
  private PharmacyRepository pharmacyRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private DrugRepository drugRepository;

  /*
   * Patient requests form to fill prescription.
   */
  @GetMapping("/prescription/fill")
  public String getfillForm(Model model) {
    model.addAttribute("prescription", new PrescriptionView());
    return "prescription_fill";
  }

  // process data from prescription_fill form
  @PostMapping("/prescription/fill")
  public String processFillForm(PrescriptionView p, Model model) {

    try {
      // find the prescription by ID
      Prescription rx = prescriptionRepository.findByRxid(p.getRxid());
      if (rx == null) {
        model.addAttribute("message", "Error: Prescription not found.");
        model.addAttribute("prescription", p);
        return "prescription_fill";
      }

      // find patient by last name
      Patient patient = patientRepository.findByLastName(p.getPatientLastName());
      if (patient == null) {
        model.addAttribute("message", "Error: Patient not found.");
        model.addAttribute("prescription", p);
        return "prescription_fill";
      }

      // find pharmacy by name and address
      Pharmacy pharmacy = pharmacyRepository.findByNameAndAddress(
          p.getPharmacyName().trim(),
          p.getPharmacyAddress().trim());
      if (pharmacy == null) {
        model.addAttribute("message", "Error: Pharmacy was not found.");
        model.addAttribute("prescription", p);
        return "prescription_fill";
      }

      if (p.getRxid() == 0) {
        model.addAttribute("message", "Error: rx ID is required.");
        model.addAttribute("prescription", p);
        return "prescription_fill";
      }

      // check if refills are available
      int fillsSoFar = 0;
      if (rx.getFills() != null) {
        fillsSoFar = rx.getFills().size();
      }

      int refillsAllowed = rx.getRefills();
      int refillsUsed = fillsSoFar - 1;

      if (refillsUsed < 0) {
        refillsUsed = 0;
      }

      if (refillsUsed >= refillsAllowed) {
        model.addAttribute("message", "Error: No refills remaining.");
        model.addAttribute("prescription", p);
        return "prescription_fill";
      }

      // total cost
      double totalCost = 0.0;
      if (pharmacy.getDrugCosts() != null) {
        for (Pharmacy.DrugCost drugCost : pharmacy.getDrugCosts()) {
          if (drugCost.getDrugName().equalsIgnoreCase(rx.getDrugName())) {
            totalCost = drugCost.getCost() * rx.getQuantity();
            break;
          }
        }
      }
      p.setCost(String.format("%.2f", totalCost));

      // new fill record
      Prescription.FillRequest newFill = new Prescription.FillRequest();
      newFill.setPharmacyID(pharmacy.getId());
      newFill.setDateFilled(LocalDate.now().toString());
      newFill.setCost(p.getCost());

      if (rx.getFills() == null) {
        rx.setFills(new ArrayList<>());
      }
      rx.getFills().add(newFill);

      // save to MongoDB
      prescriptionRepository.save(rx);

      // update view data
      Doctor doctor = doctorRepository.findByLastName(patient.getPrimaryName());
      int fillsRemaining = rx.getRefills() - rx.getFills().size();

      p.setDateFilled(LocalDate.now().toString());
      p.setDoctorId(doctor.getId());
      p.setDoctorFirstName(doctor.getFirstName());
      p.setDoctorLastName(doctor.getLastName());
      p.setPatientId(patient.getId());
      p.setPatientFirstName(patient.getFirstName());
      p.setDrugName(rx.getDrugName());
      p.setQuantity(rx.getQuantity());
      p.setRefills(fillsRemaining);
      p.setPharmacyID(pharmacy.getId());
      p.setPharmacyName(pharmacy.getName());
      p.setPharmacyPhone(pharmacy.getPhone());
      model.addAttribute("message", "Prescription filled successfully.");
      model.addAttribute("prescription", p);
      return "prescription_show";

    } catch (Exception e) {
      e.printStackTrace();
      model.addAttribute("message", "Error: " + e.getMessage());
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }
  }
}
