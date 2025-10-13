package application.service;
import application.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component


public class IdAssigner {
    @Autowired
    private SequenceService seq;

    public void assign(Doctor d) {
        d.setId(seq.getNextSequence("DOCTOR_SEQUENCE"));
    }
    public void assign(Patient p) {
        p.setId(seq.getNextSequence("PATIENT_SEQUENCE"));
    }

    public void assign(Prescription r) {
        r.setRxid(seq.getNextSequence("RXID_SEQUENCE"));
    }

    public void assign(Pharmacy ph) {
        ph.setId(seq.getNextSequence("PHARMACY_SEQUENCE"));
    }

    public void assign(Drug d) {
        d.setId(seq.getNextSequence("DRUG_SEQUENCE"));
    }


}
