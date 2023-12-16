package userRestful;

import com.google.gson.Gson;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("users")
public class UserResource {
	UserRepository repo = new UserRepository();
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getUsers() {
		return new Gson().toJson(repo.getUsers());
	}
	@GET
	@Path("{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(@PathParam("user_id") Integer user_id) {
		return repo.getUser(user_id);
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON) 
	public User createUser(User user) {
		repo.create(user);
		return user;
	}
	@PUT
	@Path("{user_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User update(@PathParam("user_id")Integer user_id, User user) {
		repo.update(user);
		return user;
	}
	@DELETE
	@Path("{user_id}")
	public void deleteUser(@PathParam("user_id")Integer user_id) {
		repo.delete(user_id);
	}
}
