package wg.hub.Controller;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import wg.hub.Entity.Student;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "students", path = "students")
public interface StudentController extends PagingAndSortingRepository<Student, Long> {

    List<Student> findByLastName(@Param("name") String name);
    List<Student> findByFirstName(@Param("name") String name);
    List<Student> findByCourse(@Param("name") String name);
}
