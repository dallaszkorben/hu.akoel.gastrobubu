package hu.akoel.restful.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hu.akoel.restful.UserModel;

import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jayway.jsonpath.JsonPath;

public class TestUser {
	 
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    WebTarget service = client.target(getBaseURI());
    Response response;
    Form form;
    
    ArrayList<UserModel> userList = new ArrayList<>();  
    
    @Before
    public void clearUserList(){
    	userList.clear();
    }
    
    @After
    public void deleteRecords() {
    	for( UserModel userModel: userList ){
    		response = service.path("rest").path("users").path( userModel.getId().toString() ).request().accept(MediaType.APPLICATION_JSON).delete();
    	}
    	userList.clear();
    }
    
    /**
     * POST/users - record new user
     */
    @Test
    public void testPostUsers_CaptureUser(){

    	//Define parameters - name, password
    	UserModel user = new UserModel( "user1" );
    	user.setPassword( "password1" );
    	userList.add( user );
    	form =new Form();
    	form.param("name", user.getName() );
    	form.param("password", user.getPassword() );    	
    	
    	//POST/users
    	response = service.path("rest").path("users").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),Response.class);

    	//Assert status = 201
    	assertEquals( Status.CREATED.getStatusCode(), response.getStatus(), 0 );

    	Integer id = response.readEntity(Integer.class);
    	user.setId( id );
    	
    	//Assert id
    	assertTrue( id > 0 );
    	
    	//Console message
    	System.err.println( "POST/users" + " - Status: " + response.getStatus() + " Reason: " + response.getStatusInfo());
    	System.err.println( "             Id: " + id );
    	System.err.println();

    }

    /**
     * GET/users/{id} - get user
     */
    @Test
    public void testGetUserId_GetASpecificUser(){

    	//Define parameters - name, password
    	UserModel user = new UserModel(  "user1" );
    	user.setPassword( "password1" );
    	userList.add( user );
    	form =new Form();
    	form.param("name", user.getName() );
    	form.param("password", user.getPassword() );
    	
    	//POST/users
    	response = service.path("rest").path("users").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),Response.class);
    	
       	//Assert status = 201
    	assertEquals( Status.CREATED.getStatusCode(), response.getStatus(), 0 );
    	
    	user.setId( response.readEntity(Integer.class) );

    	//GET/users/{id}
    	String result = service.path("rest").path("users").path(user.getId().toString()).request().accept(MediaType.APPLICATION_JSON).get(String.class);
    	Integer id = JsonPath.read(result, "$.id");
    	
    	//Assert
    	assertEquals(user.getId(), id, 0);     	
    }
    
    /**
     * PUT/users/{id} - modify user
     */
    @Test
    public void testPutUsers_ModifyUser(){
       	//Define parameters - name, password
    	UserModel user = new UserModel( "user1" );
    	user.setPassword( "password1" );
    	userList.add( user );
    	form =new Form();
    	form.param("name", user.getName() );
    	form.param("password", user.getPassword() );
    	
    	//POST/users
    	response = service.path("rest").path("users").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),Response.class);

    	//Assert status = 201
    	assertEquals( Status.CREATED.getStatusCode(), response.getStatus(), 0 );

    	user.setId( response.readEntity(Integer.class) );

    	//PUT/users/{id}
    	form =new Form();
    	form.param("name", "blablaname" );
    	form.param("password", "blablapassword" );
        response = service.path("rest").path("users").path(user.getId().toString()).request().put(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),Response.class);

        //Assert status code
        assertEquals(Status.OK.getStatusCode(), response.getStatus(), 0);
        
        //Assert values
        UserModel resultUser = response.readEntity( UserModel.class );
        assertEquals( "blablaname", resultUser.getName());
        assertEquals( "blablapassword", resultUser.getPassword() );
    }
    
    /**
     * GET/users - get user list
     */
    @Test
    public void testGetUsers_GetList(){
   
    	userList.clear();    	
    	UserModel user;
    	
    	user = new UserModel("user1");
    	form =new Form();
    	form.param("name", user.getName() );
    	response = service.path("rest").path("users").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),Response.class);
       	//Assert status = 201
    	assertEquals( Status.CREATED.getStatusCode(), response.getStatus(), 0 );
    	user.setId( response.readEntity(Integer.class) );
    	userList.add( user );
    	
    	user = new UserModel("user2");
    	form =new Form();
    	form.param("name", user.getName() );
    	response = service.path("rest").path("users").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),Response.class);
       	//Assert status = 201
    	assertEquals( Status.CREATED.getStatusCode(), response.getStatus(), 0 );
    	user.setId( response.readEntity(Integer.class) );
    	userList.add( user );
    	
    	user = new UserModel("user3");
    	form =new Form();
    	form.param("name", user.getName() );
    	response = service.path("rest").path("users").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),Response.class);
       	//Assert status = 201
    	assertEquals( Status.CREATED.getStatusCode(), response.getStatus(), 0 );
    	user.setId( response.readEntity(Integer.class) );
    	userList.add( user );
    	    	
    	//GET/users - get user list
    	response = service.path("rest").path("users").request().accept(MediaType.APPLICATION_XML).get();
    	System.err.println("GET/users" + " - Status: " + response.getStatus() + " Reason: " + response.getStatusInfo());
    	System.err.println( "        " + service.path("rest").path("users").request().accept(MediaType.APPLICATION_JSON).get( String.class ) );
    	System.err.println();
    	
    	String result = service.path("rest").path("users").request().accept(MediaType.APPLICATION_JSON).get( String.class );
    	Integer id = JsonPath.read(result, "$[0].id");
    	assertEquals( userList.get(0).getId(), id);
    	
    	id = JsonPath.read(result, "$[1].id");
    	assertEquals( userList.get(1).getId(), id);
    	
    	id = JsonPath.read(result, "$[2].id");
    	assertEquals( userList.get(2).getId(), id);

  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost:8080/RestfulService").build();
  }
} 

