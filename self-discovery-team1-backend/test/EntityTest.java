import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wg.hub.Controller.StudentController;
import wg.hub.Entity.Student;
import wg.hub.Main;

import java.util.List;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Main.class)
public class EntityTest {

    private Student andreas;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentController studentController;

    @Before
    public void createStudent(){
        // given
        andreas = new Student("Andreas","Fieselmaier","Kunst");
        entityManager.persist(andreas);
        entityManager.flush();
    }

    @Test
    public void findByFirstNameTest(){

        // when
        List<Student> found = studentController.findByFirstName(andreas.getFirstName());

        // then
       assertThat(found.get(0).getFirstName()).isEqualTo(andreas.getFirstName());
    }

    @Test
    public void findByLastNameTest(){

        // when
        List<Student> found = studentController.findByLastName(andreas.getLastName());

        // then
        assertThat(found.get(0).getLastName()).isEqualTo(andreas.getLastName());
    }

    @Test
    public void findByCourseTest(){

        // when
        List<Student> found = studentController.findByCourse(andreas.getCourse());

        // then
        assertThat(found.get(0).getCourse()).isEqualTo(andreas.getCourse());
    }
}
