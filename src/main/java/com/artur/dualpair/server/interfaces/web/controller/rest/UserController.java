package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.SociotypeDTO;
import com.artur.dualpair.server.interfaces.dto.UserDTO;
import com.artur.dualpair.server.interfaces.dto.assembler.UserDTOAssembler;
import com.artur.dualpair.server.service.user.SocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {

    private SocialUserService socialUserService;
    private UserDTOAssembler userDTOAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/user")
    public UserDTO getUser() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDTOAssembler.toDTO(socialUserService.getUser(user.getUserId()));
    }

    @RequestMapping("/api/me")
    public Map<String, String> user(Principal principal) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        return map;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/sociotypes")
    public void setSociotypes(@RequestBody SociotypeDTO[] sociotypes) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        socialUserService.setUserSociotypes(user.getUserId(), convertToCodes(sociotypes));
    }

    private Set<Sociotype.Code1> convertToCodes(SociotypeDTO[] sociotypes) {
        Set<Sociotype.Code1> codes = new HashSet<>();
        for (SociotypeDTO sociotype : sociotypes) {
            Sociotype.Code1 code = Sociotype.Code1.valueOf(sociotype.getCode1());
            codes.add(code);
        }
        return codes;
    }

    @Autowired
    public void setSocialUserService(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }

    @Autowired
    public void setUserDTOAssembler(UserDTOAssembler userDTOAssembler) {
        this.userDTOAssembler = userDTOAssembler;
    }
}
