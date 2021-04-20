/**
 * File: RecordService.java
 * Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * update by : I. Am. A. Student 040nnnnnnn
 *
 */
package bloodbank.ejb;

import static bloodbank.entity.SecurityRole.ROLE_BY_NAME_QUERY;
import static bloodbank.entity.SecurityUser.USER_FOR_OWNING_PERSON_QUERY;
import static bloodbank.utility.MyConstants.DEFAULT_KEY_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.DEFAULT_SALT_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PARAM1;
import static bloodbank.utility.MyConstants.PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.PROPERTY_KEYSIZE;
import static bloodbank.utility.MyConstants.PROPERTY_SALTSIZE;
import static bloodbank.utility.MyConstants.PU_NAME;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;



import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.Phone;
import bloodbank.entity.SecurityRole;
import bloodbank.entity.SecurityUser;

/**
 * Stateless Singleton ejb Bean - BloodBankService
 */
@Singleton
public class BloodBankService implements Serializable {
    private static final long serialVersionUID = 1L;
        
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public <T> List<T> getAll(String queryName, Class<T> clazz) {
    	TypedQuery<T> query = em.createNamedQuery(queryName, clazz);
    	return query.getResultList();
    }

    public <T> T getById(String queryName, Class<T> clazz, int id) {
    	TypedQuery<T> query = em.createNamedQuery(queryName, clazz)
    			.setParameter("param1", id);
    	return  query.getSingleResult();
    }

    @Transactional
    public <T> T persistEntity(T newEntity) {
    	em.persist(newEntity);
    	return newEntity;
    }

    @Transactional
    public void buildUserForNewPerson(Person newPerson) {
        SecurityUser userForNewPerson = new SecurityUser();
        userForNewPerson.setUsername(
            DEFAULT_USER_PREFIX + "_" + newPerson.getFirstName() + "." + newPerson.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewPerson.setPwHash(pwHash);
        userForNewPerson.setPerson(newPerson);
        SecurityRole userRole = em.createNamedQuery(ROLE_BY_NAME_QUERY, SecurityRole.class)
            .setParameter(PARAM1, USER_ROLE).getSingleResult();
        userForNewPerson.getRoles().add(userRole);
        userRole.getUsers().add(userForNewPerson);
        em.merge(userForNewPerson);
    }

    @Transactional
    public Person setAddressFor(int id, Address newAddress) {
    	return null;
    }

    /**
     * to update a person
     * 
     * @param id - id of entity to update
     * @param personWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Person updatePersonById(int id, Person personWithUpdates) {
        Person personToBeUpdated = getById(Person.SINGLE_PERSON_QUERY_NAME, Person.class, id);
        if (personToBeUpdated != null) {
            em.refresh(personToBeUpdated);
            em.merge(personWithUpdates);
            em.flush();
        }
        return personToBeUpdated;
    }
    
    @Transactional
    public BloodBank updateBloodBank(int bbID, BloodBank bloodBankWithUpdates) {
		BloodBank bloodBankToBeUpdated = getById(BloodBank.SINGLE_BLOOD_BANK_QUERY_NAME, BloodBank.class, bbID);
        if (bloodBankToBeUpdated != null) {
            em.refresh(bloodBankToBeUpdated);
            em.merge(bloodBankWithUpdates);
            em.flush();
        }
        return bloodBankToBeUpdated;
		
	}

    public boolean isDuplicated(BloodBank newBloodBank) {
    	TypedQuery<Long> allStoresQuery = em.createNamedQuery(BloodBank.IS_DUPLICATE_QUERY_NAME,Long.class);
    	allStoresQuery.setParameter("param1", newBloodBank.getName());
    	return allStoresQuery.getSingleResult()>=1;
    }
    /**
     * to delete a person by id
     * 
     * @param id - person id to delete
     */
    @Transactional
    public Person deletePersonById(int id) {
        Person person = getById(Person.SINGLE_PERSON_QUERY_NAME, Person.class, id);
        if (person != null) {
            em.refresh(person);
            TypedQuery<SecurityUser> findUser = em
                .createNamedQuery(USER_FOR_OWNING_PERSON_QUERY, SecurityUser.class)
                .setParameter(PARAM1, person.getId());
            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(person);
            return person;
        }
		return null;
    }
	
    @Transactional
    public Phone deletePhoneById(int id) {
        Phone phone = getById(Phone.SINGLE_PHONE_QUERY_NAME, Phone.class, id);
        if (phone != null) {
            em.refresh(phone);            
            em.remove(phone);
            return phone;
        }
		return null;
    }

    @Transactional
	public Address deleteAddressById(int id) {
		Address address = getById(Address.SINGLE_ADDRESS_QUERY_NAME, Address.class, id);
        if (address != null) {
            em.refresh(address);
            em.remove(address);
            return address;
        }
		return null;
	}

    @Transactional
	public BloodBank deleteBloodBankById(int id) {
		
		BloodBank bloodBank = getById(BloodBank.SINGLE_BLOOD_BANK_QUERY_NAME, BloodBank.class, id);
		
        if (bloodBank != null) {
        	
        	Set<BloodDonation> donations = bloodBank.getDonations();
        	
        	donations.forEach( bd -> {
        		deleteBloodDonationById(bd.getId());
        	});
        	
            em.refresh(bloodBank);
            em.remove(bloodBank);
        }
		return bloodBank;
	}
	
    @Transactional
	public BloodDonation deleteBloodDonationById(int id) {
		BloodDonation bloodDonation = getById(BloodDonation.SINGLE_BLOOD_DONATION_QUERY_NAME, BloodDonation.class, id);
        if (bloodDonation != null) {
        	
			if(bloodDonation.getRecord()!=null) {
				DonationRecord dr =  getById(DonationRecord.SINGLE_BLOOD_RECORD_QUERY_NAME, DonationRecord.class, bloodDonation.getRecord().getId() );
				dr.setDonation(null);
				em.merge(dr);
			}
            em.refresh(bloodDonation);
            em.remove(bloodDonation);
        }
		return bloodDonation;
	}
    
    @Transactional
	public DonationRecord deleteDonationRecordById(int id) {
    	DonationRecord donationRecord = getById(DonationRecord.SINGLE_BLOOD_RECORD_QUERY_NAME, DonationRecord.class, id);
        if (donationRecord != null) {
        	
            em.refresh(donationRecord);
            em.remove(donationRecord);
        }
		return donationRecord;
	}

    @Transactional
	public Person updatePerson(int personID, Person personWithUpdates) {
    	Person personToBeUpdated = getById(Person.SINGLE_PERSON_QUERY_NAME, Person.class, personID);
        if (personToBeUpdated != null) {
            em.refresh(personToBeUpdated);
            em.merge(personWithUpdates);
            em.flush();
        }
        return personToBeUpdated;
		
	}

}