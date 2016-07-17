package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.SearchParametersDTO;
import com.artur.dualpair.server.interfaces.dto.SociotypeDTO;
import com.artur.dualpair.server.interfaces.dto.UserDTO;
import com.artur.dualpair.server.interfaces.dto.assembler.SearchParametersDTOAssembler;
import com.artur.dualpair.server.interfaces.dto.assembler.UserDTOAssembler;
import com.artur.dualpair.server.service.user.SocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private SocialUserService socialUserService;
    private UserDTOAssembler userDTOAssembler;
    private SearchParametersDTOAssembler searchParametersDTOAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/user")
    public UserDTO getUser() {
        return userDTOAssembler.toDTO(socialUserService.getUser(getUserPrincipal().getUserId()));
    }

    @RequestMapping("/api/me")
    public Map<String, String> user(Principal principal) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        return map;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/sociotypes")
    public ResponseEntity setSociotypes(@RequestBody SociotypeDTO[] sociotypes) throws URISyntaxException {
        socialUserService.setUserSociotypes(getUserPrincipal().getUserId(), convertToCodes(sociotypes));
        return ResponseEntity.created(new URI("/api/user")).build();
    }

    private Set<Sociotype.Code1> convertToCodes(SociotypeDTO[] sociotypes) {
        Set<Sociotype.Code1> codes = new HashSet<>();
        for (SociotypeDTO sociotype : sociotypes) {
            Sociotype.Code1 code = Sociotype.Code1.valueOf(sociotype.getCode1());
            codes.add(code);
        }
        return codes;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/date-of-birth")
    public ResponseEntity setDateOfBirth(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth) throws URISyntaxException {
        socialUserService.setUserDateOfBirth(getUserPrincipal().getUserId(), dateOfBirth);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI("/api/user")).build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/search-parameters")
    public ResponseEntity setSearchParameters(@RequestBody SearchParametersDTO searchParameters) throws URISyntaxException {
        socialUserService.setUserSearchParameters(getUserPrincipal().getUsername(), searchParametersDTOAssembler.toEntity(searchParameters));
        return ResponseEntity.created(new URI("/api/user")).build();
    }

    private User getUserPrincipal() {
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Autowired
    public void setSocialUserService(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }

    @Autowired
    public void setUserDTOAssembler(UserDTOAssembler userDTOAssembler) {
        this.userDTOAssembler = userDTOAssembler;
    }

    @Autowired
    public void setSearchParametersDTOAssembler(SearchParametersDTOAssembler searchParametersDTOAssembler) {
        this.searchParametersDTOAssembler = searchParametersDTOAssembler;
    }
}
