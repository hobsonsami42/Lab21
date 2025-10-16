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

    /*
     * Patient requests form to fill prescription.
     */
    @GetMapping("/prescription/fill")
    public String getfillForm(Model model) {
        model.addAttribute("prescription", new PrescriptionView());
        return "prescription_fill";
    }


    @PostMapping("/prescription/fill")
    public String processFillForm(PrescriptionView p, Model model) {

        try {
            // find the prescription by ID
            Optional<Prescription> optRx = prescriptionRepository.findById(p.getRxid());
            if (!optRx.isPresent()) {
                model.addAttribute("message", "Error: Prescription not found.");
                model.addAttribute("prescription", p);
                return "prescription_fill";
            }
            Prescription rx = optRx.get();

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

            // find pharmacy by name and address
            Pharmacy pharmacy = pharmacyRepository.findByNameAndAddress(
                    p.getPharmacyName().trim(),
                    p.getPharmacyAddress().trim());
            if (pharmacy == null) {
                model.addAttribute("message", "Error: Pharmacy was not found.");
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
            p.setDateFilled(LocalDate.now().toString());
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
