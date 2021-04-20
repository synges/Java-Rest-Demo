/**
 * File: BloodBankResource.java Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.BLOOD_DONATION_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;


import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.BloodDonation;

@Path( BLOOD_DONATION_RESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class BloodDonationResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@GET
	public Response getBloodDonationss() {
		List< BloodDonation> bloodDonations = service.getAll(BloodDonation.ALL_BLOOD_DONATIONS_QUERY_NAME, BloodDonation.class);
		Response response = Response.ok( bloodDonations).build();
		return response;
	}

	@GET
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getStoreById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {

		BloodDonation bloodDonation = service.getById(BloodDonation.SINGLE_BLOOD_DONATION_QUERY_NAME, BloodDonation.class, id);
		Response response = Response.ok( bloodDonation).build();
		return response;
	}

	
	@RolesAllowed( { ADMIN_ROLE})
	@DELETE
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deleteBloodDonation( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "update a specific BloodDonation ...");
		BloodDonation bd = service.deleteBloodDonationById( id);
		Response response  = Response.ok( bd).build();
		return response;
	}

}