/**
 * File: PhoneResource.java Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.PHONE_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.ws.rs.core.Response.Status;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.Phone;
import bloodbank.entity.Phone;
import bloodbank.entity.SecurityUser;

@Path(PHONE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PhoneResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getPhones() {
		LOG.debug("retrieving all phones ...");
		List<Phone> phones = service.getAll(Phone.ALL_PHONES_QUERY_NAME, Phone.class);
		Response response = Response.ok(phones).build();
		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getPhoneById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific phone " + id);
		Response response = null;
		Phone phone = null;

		if (sc.isCallerInRole(ADMIN_ROLE)) {
			phone = service.getById(Phone.SINGLE_PHONE_QUERY_NAME, Phone.class, id);
			response = Response.status(phone == null ? Status.NOT_FOUND : Status.OK).entity(phone).build();
		} else {
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}

	@POST
	@RolesAllowed({ ADMIN_ROLE })
	public Response addPhone(Phone newPhone) {
		Response response = null;
		Phone newPhoneWithIdTimestamps = service.persistEntity(newPhone);
		response = Response.ok(newPhoneWithIdTimestamps).build();
		return response;
	}

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deletePhone(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		response = Response.ok(service.deletePhoneById(id)).build();
		return response;
	}
}