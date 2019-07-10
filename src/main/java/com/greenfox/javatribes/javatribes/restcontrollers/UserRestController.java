package com.greenfox.javatribes.javatribes.restcontrollers;

import com.greenfox.javatribes.javatribes.dto.RegisterObject;
import com.greenfox.javatribes.javatribes.dto.ResponseObject;
import com.greenfox.javatribes.javatribes.exceptions.CustomException;
import com.greenfox.javatribes.javatribes.model.Kingdom;
import com.greenfox.javatribes.javatribes.model.User;
import com.greenfox.javatribes.javatribes.security.JwtTokenProvider;
import com.greenfox.javatribes.javatribes.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserRestController {

    private UserService userService;
    private JwtTokenProvider jwtTokenProvider;

    public UserRestController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody @Valid User user) throws CustomException {

        User authUser = userService.findByUsernameAndPassword(user.getUsername(), user.getPassword());

        return ResponseEntity.status(HttpStatus.valueOf(200)).body(new ResponseObject("ok",
                null, jwtTokenProvider.createToken(authUser.getUsername(), authUser.getRoles())));
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody @Valid RegisterObject registerObject) throws CustomException {

        if(registerObject.getKingdom().isEmpty()) {
            registerObject.setKingdom(registerObject.getUsername() + "'s kingdom");
        }
        User newUser = new User(registerObject.getUsername(), registerObject.getPassword(), new Kingdom(registerObject.getKingdom()));
        userService.saveUser(newUser);

        return ResponseEntity.status(HttpStatus.valueOf(200)).body(newUser);
    }
}

//IF WE WANT TO USE JSON FILTER
//        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
//        filterProvider.addFilter("KingdomFilter", SimpleBeanPropertyFilter.filterOutAllExcept("kingdomId"));
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setFilterProvider(filterProvider);
//        String jsonData = mapper.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(newUser);