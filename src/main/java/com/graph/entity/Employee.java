package com.graph.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@ToString
@Data
@Document(collection = "employees")
public class Employee {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<String> roles;
    private String organizationId;
    private boolean isUsingMFA;
    private String secret;
    private boolean isAlreadyRegistered;
    private String userIcon;
    private String employeeId;
    private String departmentId;
    private String phone;
    private String languages;
    private String location;
    private String zipCode;
    private List<String> skills;
    private String biography;
    private String organizationRole;
    private String shiftId;
    private String shiftType;
    private String startTime;
    private String endTime;
    public boolean enableShift = Boolean.FALSE;
    public boolean productivityAccess = Boolean.TRUE;
    private boolean enableOverTime = Boolean.FALSE;
    private boolean isSessionRecording;

}