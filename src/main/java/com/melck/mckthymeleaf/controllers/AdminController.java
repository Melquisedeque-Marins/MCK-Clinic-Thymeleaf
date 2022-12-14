package com.melck.mckthymeleaf.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.melck.mckthymeleaf.models.Scheduling;
import com.melck.mckthymeleaf.models.doctor.Doctor;
import com.melck.mckthymeleaf.models.user.User;
import com.melck.mckthymeleaf.services.DoctorService;
import com.melck.mckthymeleaf.services.SchedulingService;
import com.melck.mckthymeleaf.services.UserService;


@Controller
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private UserService service;

    @Autowired
    private SchedulingService schedulingService;

    @Autowired
    private DoctorService doctorService;

    
    @GetMapping
    public ModelAndView adminScreen(){
        User user = service.userLogged();
        String[] userName = user.getName().toLowerCase().split(" ", 0);
        String firstName = userName[0];
        ModelAndView mv = new ModelAndView("/adminArea");
        mv.addObject("userLogged", user);
        mv.addObject("userName", firstName);
        return mv;
    }
    /*
    @GetMapping("/users")
    public ModelAndView findAll(){
        ModelAndView mv = new ModelAndView("/clients/clientsList");
        mv.addObject("listClients", service.findAll());
        return mv;
    }
     */

    @GetMapping("/users")
    public ModelAndView findUserPerCpf(
            @RequestParam(value = "cpf", defaultValue = "") String cpf,
            Pageable pageable
    ) {
        Page<User> page = service.findUser(cpf.trim(), pageable);
        ModelAndView mv = new ModelAndView("/clients/clientsList");
        mv.addObject("listClients", page);
        return mv;
    }

    @GetMapping("/details/users/{userId}")
    public ModelAndView userDetails(@PathVariable Long userId, Pageable pageable){
        User user = service.findById(userId);
        Page<Scheduling> schedulings  = schedulingService.findAllByUser(pageable);
        ModelAndView mv = new ModelAndView("/clients/clientsList");
        mv.addObject("user", user);
        mv.addObject("schedulings", schedulings);
        return mv;
    }

    @DeleteMapping("/delete/{id}")
    public ModelAndView removeUser(@PathVariable Long id) {
        service.delete(id);
        return new ModelAndView("redirect:/admins/users");
    }

    @GetMapping("/doctors")
	public ModelAndView findAllDoctorsPaged(
			@RequestParam(value = "expertiseId", defaultValue = "0") Long expertiseId,
			@RequestParam(value = "name", defaultValue = "") String name,
			Pageable pageable
	) {
		Page<Doctor> page = doctorService.findAllPaged(expertiseId, name.trim(), pageable);
        ModelAndView mv = new ModelAndView("/managerDoctor");
        mv.addObject("doctors", page);
        return mv;
	}

    @GetMapping("/doctors/{id}")
    public ModelAndView findSchedulingsPerDoctor(@PathVariable Long id) {
        List<Scheduling> schedulings = schedulingService.findSchedulesPerDoctor(id);
        ModelAndView mv = new ModelAndView("/managerDoctorSchedulings");
        mv.addObject("schedulings", schedulings);
        return mv;
    }
    
    @PutMapping("/doctors/{doctorId}/schedulings/{schedulingId}")
    public ModelAndView confirmScheduling(@PathVariable Long doctorId, @PathVariable Long schedulingId) {
        schedulingService.updateStatus(schedulingId);
        return new ModelAndView("redirect:/admins/doctors/{doctorId}");
    }

    @GetMapping("/schedulings/users/{id}")
    public ModelAndView schedulingsPerClient(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable){
        ModelAndView mv = new ModelAndView("/clients/clientsSchedulings");
        Page<Scheduling> schedulings  = schedulingService.findAllByUser(id, pageable);
        User client = service.findById(id);
        int number = schedulings.getPageable().getPageNumber();
        mv.addObject("client", client);
        mv.addObject("schedulings", schedulings);
        mv.addObject("number", number);
        return mv;
    }


}
