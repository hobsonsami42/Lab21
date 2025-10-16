package application;
import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */

@Controller
public class ControllerPatientUpdate {
  @Autowired
  PatientRepository patientRepository;
  @Autowired
  private DoctorRepository doctorRepository;

  /*
   *  Display patient profile for patient id.
   */
  @GetMapping("/patient/edit/{id}/{lastName}")
  public String getUpdateForm(@PathVariable int id, @PathVariable String lastName, Model model) {
    System.out.println("getUpdateForm " + id);

    PatientView pv = new PatientView();
    pv.setId(id);

    Patient patient = patientRepository.findByIdAndLastName(id, lastName);
    if (patient != null) {
      pv.setLastName(patient.getLastName());
      pv.setFirstName(patient.getFirstName());
      pv.setStreet(patient.getStreet());
      pv.setCity(patient.getCity());
      pv.setState(patient.getState());
      pv.setZipcode(patient.getZipcode());
      pv.setBirthdate(patient.getBirthdate());
      pv.setPrimaryName(patient.getPrimaryName());

      model.addAttribute("message", "Editing profile for patient ID " + id);
      model.addAttribute("patient", pv);
      return "patient_edit";
    } else {
      model.addAttribute("message", "Patient not found for ID " + id);
      return "index";
    }
  }

  /*
   * Process changes from patient_edit form
   *  Primary doctor, street, city, state, zip can be changed
   *  ssn, patient id, name, birthdate, ssn are read only in template.
   */
  @PostMapping("/patient/edit")
  public String updatePatient(PatientView p, Model model) {
    String doctorPrimaryName = p.getPrimaryName();
    Doctor doctor = doctorRepository.findByLastName(doctorPrimaryName);

    if (doctor == null) {
      model.addAttribute("message",
          "Doctor with last name '" + doctorPrimaryName + "' not found.");
      model.addAttribute("patient", p);
      return "patient_edit";
    }

    Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());
    if (patient != null) {
      patient.setStreet(p.getStreet());
      patient.setCity(p.getCity());
      patient.setState(p.getState());
      patient.setZipcode(p.getZipcode());
      patient.setPrimaryName(doctorPrimaryName);

      patientRepository.save(patient);

      model.addAttribute("message", "Profile updated for patient ID " + p.getId());
      model.addAttribute("patient", p);
      return "patient_edit";
    } else {
      model.addAttribute("message", "Patient not found for ID " + p.getId());
      return "index";
    }
  }
}
