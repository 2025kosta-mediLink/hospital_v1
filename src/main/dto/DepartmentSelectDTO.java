package dto;

import java.util.List;
import java.util.ArrayList;

public class DepartmentSelectDTO {
    private List<DepartmentDetailDTO> departments = new ArrayList<>();

    public List<DepartmentDetailDTO> getDepartments() { return departments; }
    public void setDepartments(List<DepartmentDetailDTO> departments) { this.departments = departments; }
}