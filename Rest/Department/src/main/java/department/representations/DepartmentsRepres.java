package department.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import department.resources.Department;

import java.util.ArrayList;

public class DepartmentsRepres {
    public final ArrayList<DepartmentRepres> departments;

    @JsonCreator
    public DepartmentsRepres(@JsonProperty("departments") ArrayList<Department> deps){
        departments = new ArrayList<>();
        for(Department d : deps){
            DepartmentRepres dr = new DepartmentRepres(d.getName(), d.getDescription());
            departments.add(dr);
        }
    }
}
