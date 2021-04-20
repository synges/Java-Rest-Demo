/**
 * File: BloodBankResource.java Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.BLOODBANK_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;

@Path( BLOODBANK_RESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class BloodBankResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@GET
	public Response getBloodBanks() {
		LOG.debug( "retrieving all bloodbanks...");
		List< BloodBank> bloodbanks = service.getAll(BloodBank.ALL_BLOOD_BANKS_QUERY_NAME, BloodBank.class);
		LOG.trace( "found={}", bloodbanks);
		Response response = Response.ok( bloodbanks).build();
		return response;
	}

	@GET
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getStoreById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific bloodbank with id=" + id);
		BloodBank bloodbank = service.getById(BloodBank.SINGLE_BLOOD_BANK_QUERY_NAME, BloodBank.class, id);
		Response response = Response.ok( bloodbank).build();
		return response;
	}

	/**
	 * response that displays for adding new BloodBank
	 * 
	 * @param newBloodbank
	 * @return Response
	 */
	@RolesAllowed( { ADMIN_ROLE })
	@POST
	public Response addBloodBank( BloodBank newBloodbank) {
		LOG.debug( "add a new bloodbank ...");
		if(service.isDuplicated( newBloodbank)) {
			HttpErrorResponse er = new HttpErrorResponse( Status.CONFLICT.getStatusCode(), "entity already exists");
			return Response.status( Status.CONFLICT).entity( er).build();
		}else {
			BloodBank tempBloodBank = service.persistEntity( newBloodbank);
			return Response.ok( tempBloodBank).build();
		}
	}
	
	@RolesAllowed( { ADMIN_ROLE })
	@POST
	@Path("/{id}/blooddonation")
	public Response addBloodDonationToBloodBank( @PathParam("id") int bbID, BloodDonation newBloodDonation) {
		LOG.debug( "add a new BloodDonation to bloodbank={} ...", bbID);
		
		BloodBank bb = service.getById(BloodBank.SINGLE_BLOOD_BANK_QUERY_NAME, BloodBank.class, bbID);
		newBloodDonation.setBank( bb);
		bb.getDonations().add( newBloodDonation);
		service.updateBloodBank( bbID, bb);
		
		return Response.ok( bb).build();
	}

	
	@RolesAllowed( { ADMIN_ROLE})
	@DELETE
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deleteBloodBank( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "update a specific BloodBank ...");
//    	BloodBank bb = service.getBloodBankById( id);
		BloodBank bb = service.deleteBloodBankById( id);
		Response response  = Response.ok( bb).build();
		return response;
	}

}