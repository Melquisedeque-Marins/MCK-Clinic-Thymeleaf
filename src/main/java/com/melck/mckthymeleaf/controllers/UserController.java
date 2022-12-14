package com.melck.mckthymeleaf.controllers;

import com.melck.mckthymeleaf.dtos.UserDTO;
import com.melck.mckthymeleaf.models.Scheduling;
import com.melck.mckthymeleaf.models.enums.Gender;
import com.melck.mckthymeleaf.models.enums.Status;
import com.melck.mckthymeleaf.models.user.User;
import com.melck.mckthymeleaf.services.SchedulingService;
import com.melck.mckthymeleaf.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService service;

    @Autowired
    private SchedulingService schedulingService;

    
    @GetMapping("/form")
    public ModelAndView formClient(UserDTO userDTO){
        ModelAndView mv = new ModelAndView("/clients/formClient");
        mv.addObject("listGender", Gender.values());
        return mv;
    }
    
    @PostMapping ("/form")
    public ModelAndView insertClient(@Valid UserDTO userDTO, BindingResult br){
        if (br.hasErrors()){
            ModelAndView mv = new ModelAndView("/clients/formClient");
            mv.addObject("listGender", Gender.values());
            return mv;
        }
        service.insert(userDTO);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping
    public ModelAndView loggedArea(Pageable pageable){
        ModelAndView mv = new ModelAndView("/clients/clientArea");
        User user = service.userLogged();
        Page<Scheduling> schedulings  = schedulingService.findAllByUser(pageable);
        String[] userName = user.getName().toLowerCase().split(" ", 0);
        String firstName = userName[0];
        mv.addObject("userLogged", user);
        mv.addObject("userName", firstName);
        mv.addObject("schedulings", schedulings);
        mv.addObject("listStatus", Status.values());
        return mv;
    }

    @GetMapping("/schedulings")
    public ModelAndView schedulingHistoryByUser(@PageableDefault(size = 10) Pageable pageable){
        ModelAndView mv = new ModelAndView("/clients/clientAreaAllSchedulings");
        User user = service.userLogged();
        Page<Scheduling> schedulings  = schedulingService.findAllByUser(pageable);
        String[] userName = user.getName().toLowerCase().split(" ", 0);
        String firstName = userName[0];
        mv.addObject("userLogged", user);
        mv.addObject("userName", firstName);
        mv.addObject("schedulings", schedulings);
        mv.addObject("listStatus", Status.values());
        return mv;
    }

    @GetMapping("/info")
    public ModelAndView loggedAreaInfo(UserDTO userDTO){
        ModelAndView mv = new ModelAndView("/clients/clientAreaSelfInfo");
        User user = service.userLogged();
        String[] userName = user.getName().toLowerCase().split(" ", 0);
        String firstName = userName[0];
        mv.addObject("userLogged", user);
        mv.addObject("userName", firstName);
        return mv;
    }

    @GetMapping("/info/update")
    public ModelAndView updateInfo(UserDTO userDTO){
        ModelAndView mv = new ModelAndView("/clients/clientAreaUpdateInfo");
        User user = service.userLogged();
        String[] userName = user.getName().toLowerCase().split(" ", 0);
        String firstName = userName[0];
        mv.addObject("userLogged", user);
        mv.addObject("userName", firstName);
        return mv;
    }

    @GetMapping("/update")
    public ModelAndView updateUserInfo(UserDTO userDTO){
        service.updateUser(userDTO);
        return new ModelAndView("redirect:/users/info");
    }

}
