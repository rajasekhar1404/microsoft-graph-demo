package com.graph.controller;

import com.graph.entity.Employee;
import com.graph.entity.OnBoardParams;
import com.graph.entity.Token;
import com.graph.service.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserServiceImpl userService;

    UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public void getUsers() throws Exception {
        Token token = Token.builder().build();
        OnBoardParams params = new OnBoardParams();
        params.setId("63dfa2e1bad3a07c23e09647");
        userService.onBoardUsers(token, params);
    }

    @PostMapping("/addOrg")
    public OnBoardParams addOrg(@RequestBody OnBoardParams params) {
        return userService.addOrganization(params);
    }

    @PatchMapping("/find")
    public void updateEmployee(@RequestBody Employee employee) throws Exception {
        Token token = Token.builder().organizationId("63dfa2e1bad3a07c23e09647").build();
        userService.updateEmployee(token, employee);
    }

    @GetMapping("/dept")
    public void getDepartments() throws Exception {
        Token token = Token.builder().organizationId("63dfa2e1bad3a07c23e09647")
                .userId("cfdc0846-8801-485e-8036-747afcaf2114")
                .build();
        userService.getDepartment(token);
    }

    @GetMapping("/addusertogroup")
    public void addUserToGroup() throws Exception {
        Token token = Token.builder().organizationId("63dfa2e1bad3a07c23e09647")
                .userId("cfdc0846-8801-485e-8036-747afcaf2114")
                .build();
        Employee employee = new Employee();
        employee.setEmployeeId("123f5c64-522b-4240-9a52-b5ba53008cb2");
        userService.addUserToAzureAD(token, employee);
    }

    @GetMapping("/removeuserfromgroup")
    public void reomveUserFromGroup() throws Exception {
        Token token = Token.builder().organizationId("63dfa2e1bad3a07c23e09647")
                .userId("cfdc0846-8801-485e-8036-747afcaf2114")
                .build();
        Employee employee = new Employee();
        employee.setEmployeeId("123f5c64-522b-4240-9a52-b5ba53008cb2");
        userService.removeUserFromAzureAD(token, employee);
    }

}
