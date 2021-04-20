/**
 * File: AddressResource.java Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADDRESS_RESOURCE_NAME;
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
import bloodbank.entity.Address;

@Path(ADDRESS_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AddressResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getAddresss() {
		LOG.debug("retrieving all addresses ...");
		List<Address> addresses = service.getAll(Address.ALL_ADDRESSES_QUERY_NAME, Address.class);
		Response response = Response.ok(addresses).build();
		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getAddressById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific address " + id);
		Response response = null;
		Address address = null;

		if (sc.isCallerInRole(ADMIN_ROLE)) {
			address = service.getById(Address.SINGLE_ADDRESS_QUERY_NAME, Address.class, id);
			response = Response.status(address == null ? Status.NOT_FOUND : Status.OK).entity(address).build();
		} else {
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}

	@POST
	@RolesAllowed({ ADMIN_ROLE })
	public Response addAddress(Address newAddress) {
		Response response = null;
		Address newAddressWithIdTimestamps = service.persistEntity(newAddress);
		response = Response.ok(newAddressWithIdTimestamps).build();
		return response;
	}

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deleteAddress(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		response = Response.ok(service.deleteAddressById(id)).build();
		return response;
	}
}