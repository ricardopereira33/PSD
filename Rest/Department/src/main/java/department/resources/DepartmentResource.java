package department.resources;

import department.representations.DepartmentRepres;
import department.representations.DepartmentsRepres;
import io.dropwizard.jersey.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartmentResource {
    private HashMap<String, Department> departments;

    public DepartmentResource() {
        this.departments = new HashMap<>();
    }

    @GET
    @Path("/departments")
    public DepartmentsRepres get() {
        ArrayList<Department> deps = (ArrayList<Department>) departments.values();
        return DepartmentsRepres(deps);
    }

    @GET
    @Path("/department/{name}")
    public DepartmentRepres get(@Path("name") String name) {
        Department d = departments.get(name);

        return new DepartmentRepres(d.getName(),d.getDescription());
    }

    @PUT
    @Path("/department/{name}")
    public Response put(@PathParam("name") String name, Department d) {
        departments.put(name, d);
        return Response.ok().build();

    }

    @POST
    @Path("/{name}")
    public Response delete(@PathParam("name") String name) {
        if(departments.containsKey(name)){
            departments.remove(name);
            return Response.ok().build();
        }
        else
            return Response.noContent().build();
    }


}
