package application.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
 * Entity class for prescription and fill request data to mongo collection
 */

@Document(collection = "prescription")
public class Prescription {
	
	public static class FillRequest {
		
		private int pharmacyID;    
		private LocalDate dateFilled;
		private String cost;
		
		
		public int getPharmacyID() {
			return pharmacyID;
		}
		public void setPharmacyID(int pharmacyID) {
			this.pharmacyID = pharmacyID;
		}
		public LocalDate getDateFilled() {
			return dateFilled;
		}
		public void setDateFilled(LocalDate dateFilled) {
			this.dateFilled = dateFilled;
		}
		public String getCost() {
			return cost;
		}
		public void setCost(String cost) {
			this.cost = cost;
		}
		@Override
		public String toString() {
			return "PrescriptionFill [pharmacyID=" + pharmacyID + ", dateFilled=" + dateFilled + ", cost=" + cost + "]";
		}
	}
	
	@Id
	private int rxid;   
	// following fields are set when doctor creates a prescription.
	private String drugName;
	private int quantity;
	private int patientId;
	private int doctorId;
	private String dateCreated;
	private int refills;
	private ArrayList<FillRequest> fills = new ArrayList<>();
	private int pharmacyId;
    private String pharmacyName;
    private String pharmacyAddress;
    private String pharmacyPhone;
    private double cost;
    private LocalDate dateFilled;

    public LocalDate getDateFilled() {
        return dateFilled;
    }
    public void setDateFilled(LocalDate dateFilled) {
        this.dateFilled = dateFilled;
    }

    public int getPharmacyId() {
        return pharmacyId;
    }
    public void setPharmacyId(int pharmacyId) {
        this.pharmacyId = pharmacyId;
    }
    public String getPharmacyName() {
        return pharmacyName;
    }
    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }
    private String getPharmacyAddress() {
        return pharmacyAddress;
    }
    public void setPharmacyAddress(String pharmacyAddress) {
        this.pharmacyAddress = pharmacyAddress;
    }
    public String getPharmacyPhone() {
        return pharmacyPhone;
    }
    public void setPharmacyPhone(String pharmacyPhone) {
        this.pharmacyPhone = pharmacyPhone;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
	public int getRxid() {
		return rxid;
	}
	public void setRxid(int rxid) {
		this.rxid = rxid;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public int getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public int getRefills() {
		return refills;
	}
	public void setRefills(int refills) {
		this.refills = refills;
	}
	public ArrayList<FillRequest> getFills() {
		return fills;
	}
	public void setFills(ArrayList<FillRequest> fills) {
		this.fills = fills;
	}
	@Override
	public String toString() {
		return "PrescriptionData [rxid=" + rxid + ", drugName=" + drugName + ", quantity=" + quantity + ", patientId="
				+ patientId + ", doctorId=" + doctorId + ", dateCreated=" + dateCreated + ", refills=" + refills
				+ ", fills=" + fills + "]";
	}

}
