package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */

@Controller
public class ControllerPatientCreate {

  @Autowired
  PatientRepository patientRepository;

  @Autowired
  SequenceService sequence;
  @Autowired
  private DoctorRepository doctorRepository;

  /*
   * Request blank patient registration form.
   */
  @GetMapping("/patient/new")
  public String getNewPatientForm(Model model) {
    // return blank form for new patient registration
    model.addAttribute("patient", new PatientView());
    return "patient_register";
  }

  /*
   * Process data from the patient_register form
   */
  @PostMapping("/patient/new")
  public String createPatient(PatientView p, Model model) {

    // validate doctor last name and find the doctor id
    Doctor doc;
    try {
      doc = doctorRepository.findByLastName(p.getPrimaryName());

      if (doc == null) {
        model.addAttribute("message", "Error: Doctor with last name '"
            + p.getPrimaryName() + "' not found.");
        model.addAttribute("patient", p);
        return "patient_register";
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // get the next unique id for patient.
    int id = sequence.getNextSequence("PATIENT_SEQUENCE");
    // create a model.patient instance
    // copy data from PatientView to model
    Patient patientM = new Patient();
    patientM.setId(id);
    patientM.setSsn(p.getSsn());
    patientM.setFirstName(p.getFirstName());
    patientM.setLastName(p.getLastName());
    patientM.setStreet(p.getStreet());
    patientM.setCity(p.getCity());
    patientM.setState(p.getState());
    patientM.setZipcode(p.getZipcode());
    patientM.setBirthdate(p.getBirthdate());
    patientM.setPrimaryName(doc.getLastName());
    patientRepository.insert(patientM);

    // display message and patient information
    model.addAttribute("message", "Registration successful.");
    model.addAttribute("patient", p);
    return "patient_show";
  }

  /*
   * Request blank form to search for patient by id and name
   */
  @GetMapping("/patient/edit")
  public String getSearchForm(Model model) {
    model.addAttribute("patient", new PatientView());
    return "patient_get";
  }

  /*
   * Perform search for patient by patient id and name.
   */
  @PostMapping("/patient/show")
  public String showPatient(PatientView p, Model model) {

    // retrieve patient using the id, last_name entered by user
    Patient patientM = patientRepository.findByIdAndLastName(p.getId(),
        p.getLastName());

    if (patientM != null) {
      p.setId(patientM.getId());
      p.setFirstName(patientM.getFirstName());
      p.setStreet(patientM.getStreet());
      p.setCity(patientM.getCity());
      p.setState(patientM.getState());
      p.setZipcode(patientM.getZipcode());
      p.setBirthdate(patientM.getBirthdate());
      p.setPrimaryName(patientM.getPrimaryName());

      model.addAttribute("message", "Patient found.");
      model.addAttribute("patient", p);
      return "patient_show";
    } else {
      model.addAttribute("message", "Patient not found.");
      model.addAttribute("patient", new PatientView());
      return "patient_get";
    }
  }
}
