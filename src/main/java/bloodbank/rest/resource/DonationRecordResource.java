/**
 * File: AddressResource.java Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.DONATION_RECORD_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.DonationRecord;

@Path(DONATION_RECORD_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DonationRecordResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getRecord() {
		LOG.debug("retrieving all records ...");
		List<DonationRecord> records = service.getAll(DonationRecord.ALL_RECORDS_QUERY_NAME, DonationRecord.class);
		Response response = Response.ok(records).build();
		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getRecordById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific record " + id);
		Response response = null;
		DonationRecord record = null;

		if (sc.isCallerInRole(ADMIN_ROLE)) {
			record = service.getById(DonationRecord.SINGLE_BLOOD_RECORD_QUERY_NAME, DonationRecord.class, id);
			response = Response.status(record == null ? Status.NOT_FOUND : Status.OK).entity(record).build();
		} else {
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}



	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deleteDonationRecord(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		response = Response.ok(service.deleteDonationRecordById(id)).build();
		return response;
	}
}