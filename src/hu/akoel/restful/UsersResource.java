package hu.akoel.restful;

import javax.ejb.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

// Will map the resource to the URL users
@Stateless
@Path("/users")
public class UsersResource {
	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	@PersistenceContext(unitName="pu1")
	private EntityManager em;

	/**
     * Returns the list of Users
	 * 
     * @return
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<UserModel> getUsers() {				
		List<UserModel> users = new ArrayList<UserModel>();

		Query q = em.createQuery("SELECT u FROM UserModel u ORDER BY u.id"); 
		users = q.getResultList();

		return users;
	}

	@POST
	@Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response newUser(
			@FormParam("name") String name,
			@FormParam("password") String password,
			@Context HttpServletResponse servletResponse) throws IOException {
		UserModel user = new UserModel(name);
		if (password != null) {
			user.setPassword(password);	
		}		

		try{
			em.persist( user );
		}catch( Exception e ){
			System.err.println("Nagy baj van. nem megy a persistencia");
			throw e;
		}

		//201 - Created + Id
		Response res = Response.created(uriInfo.getAbsolutePath()).entity(user.getId()).build();
		return res;		
		//servletResponse.sendRedirect("../create_todo.html");
	}

	@Path("{userId}")
	@DELETE
	public Response deleteUser(@PathParam("userId") Integer userId) {

		try{
			UserModel user = em.find( UserModel.class, userId );
			em.persist(user);
			em.remove(user);
		}catch(Exception e ){
			//TODO standarized
			throw new RuntimeException("Delete: User with " + userId + " not found");
		}

		return Response.accepted().build();
	}

	@Path("{userId}")
	@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UserModel getUser( @PathParam("userId") Integer userId ) {

		UserModel user = em.find(UserModel.class, userId);

		if (user == null)			
			//TODO make it standarized
			throw new RuntimeException("Get: User with " + userId + " not found");
		return user;
	}

	@Path("{userId}")
	@PUT	
	@Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response putUser(
			@PathParam("userId") Integer userId,
			@FormParam("name") String name,
			@FormParam("password") String password,
			@Context HttpServletResponse servletResponse) {

		Response res;
		UserModel user = em.find(UserModel.class, userId );
		if( null == user ){
			res = Response.status(Status.NOT_FOUND).build();
		}else{
			if( name != null && !name.isEmpty() ){
				user.setName(name);
			}
			if( password != null && !password.isEmpty() ){
				user.setPassword(password);
			}
			em.persist( user );
			res = Response.status(Status.OK ).entity(user).build();
		}
		return res;
	}
} 
