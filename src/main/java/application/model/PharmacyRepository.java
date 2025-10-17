package application.model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PharmacyRepository extends MongoRepository<Pharmacy, Integer> {
	
	Pharmacy findByNameAndAddress(String name, String address);
    List<Pharmacy> findByDrugCostsDrugName(String drugName);

}
