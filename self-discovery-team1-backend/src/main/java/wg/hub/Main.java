package wg.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wg.hub.Controller.StudentController;
import wg.hub.Entity.Student;

import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

    }

    //Fakedata
    @Bean
    public CommandLineRunner repo(StudentController repository){
        return (args) -> {
            repository.save(new Student("Timo", "Gerth", "Informatik"));
            repository.save(new Student("Andreas", "Paulick", "Englisch"));
            repository.save(new Student("Rudolf", "Grauberger", "Ballett"));
            repository.save(new Student("Anton", "Grauberger", "Raketenwissenschaften"));
            repository.save(new Student("Andreas", "Rasmussen", "Deutsch"));
            repository.save(new Student("Angela", "Mörkel", "Informatik"));
        };
    }
}

// ask eureka server for all registered services
@RestController
class ServiceInstanceRestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);

    }

    @Value("${server.port}")
    private String servicePort;

    @Value("${spring.application.name}")
    private String appName;

    @RequestMapping(value="/students/getID", method = RequestMethod.GET)
    public String getServiceID () {
        return "This is an instance of " + appName + " on port: " + servicePort;
    }
}


