package mil.dtic.openidm.controller;


import mil.dtic.openidm.dto.UserDto;
import mil.dtic.openidm.service.OpenIDMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final OpenIDMService openIDMService;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(OpenIDMService openIDMService) {
        this.openIDMService = openIDMService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") String id,
                                      @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null) {
            log.warn("No Authorization header received from client");
            // You might want to enforce auth; for now we forward null and OpenIDM will reject if required
        }
        // UserDto user = openIDMService.getUser(id, authorizationHeader);
         UserDto user = openIDMService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody String userJson,
                                        @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String result = openIDMService.createUser(userJson, authorizationHeader);
        return ResponseEntity.ok(result);
    }
}

