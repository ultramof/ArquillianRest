package de.cofinpro.ArquillianRest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.extension.rest.client.Header;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.cofinpro.ArquillianRest.User;

@RunWith(Arquillian.class)
public class RestClientTestCase {

	
	@ArquillianResource
    private URL deploymentURL;

    @Deployment(testable = false)
    public static WebArchive create()
    {
        return ShrinkWrap.create(WebArchive.class)
            .addPackage(CustomerResource.class.getPackage())
            .addClasses(CustomerResource.class, User.class, JaxRsActivator.class);
    }
	
	@Test
	@Header(name = "Authorization", value = "abcd")
	public void login(@ArquillianResteasyResource CustomerResource customerResource) {

		Response response = customerResource.login();
		assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
	}
    
	@Test
	@Header(name = "Authorization", value = "abc")
	public void getTechiById(@ArquillianResteasyResource CustomerResource customerResource) {

		// Given
		String givenName = "Michi";
		long givenId = 1L;

		// When
		final User user = customerResource.getTechiById(givenId);

		// Then
		assertNotNull(user);
		assertNotNull(user.getName());
		assertEquals(givenName, user.getName());
	}
	
	@Test
	@Header(name = "Authorization", value = "abc")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void addUser(@ArquillianResteasyResource("rest/cofinpro/createUser") ResteasyWebTarget webTarget) {
		
		//Given
		final Invocation.Builder invocationBuilder = webTarget.request();
	    invocationBuilder.acceptEncoding("UTF-8");
	    invocationBuilder.accept(MediaType.APPLICATION_JSON);
	    final Invocation invocation = invocationBuilder.buildPost(Entity.entity(new User(), MediaType.APPLICATION_JSON_TYPE));
	    
	    //      When
	    final Response response = invocation.invoke();

	    //        Then
	    assertEquals(deploymentURL + "rest/cofinpro/createUser", webTarget.getUri().toASCIIString());
	    assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
	    assertEquals(HttpStatus.SC_OK, response.getStatus());
	}
}

