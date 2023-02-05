package com.graph.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.graph.entity.Employee;
import com.graph.entity.OnBoardParams;
import com.graph.entity.Token;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.GroupCollectionPage;
import com.microsoft.graph.requests.UserCollectionPage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl {

    private final MongoTemplate mongoTemplate;
    private final static String ORG_COLLECTION = "orgSettings";
    private final static String USER_COLLECTION = "user";

    public UserServiceImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void onBoardUsers(Token token, OnBoardParams params) throws Exception {

        GraphServiceClient client = buildClient(params.getId());

        UserCollectionPage users = client.users().buildRequest().get();

        for (User user : users.getCurrentPage()) {
            Employee userToSave = new Employee();
            userToSave.setEmail(user.userPrincipalName);
            userToSave.setFirstName(user.givenName);
            userToSave.setLastName(user.surname);
            userToSave.setEmployeeId(user.employeeId);
            writeUsersToDB(userToSave);
        }

    }

    public GraphServiceClient buildClient(String orgId) throws Exception {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(orgId)));

        Optional<OnBoardParams> optionalSettings = Optional.ofNullable(mongoTemplate.findOne(query, OnBoardParams.class, ORG_COLLECTION));

        if (!optionalSettings.isPresent()) {
            throw new Exception("Invalid Organization id");
        }

        OnBoardParams settings = optionalSettings.get();

        try {
            ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                    .clientId(settings.getClientId())
                    .clientSecret(settings.getSecret())
                    .tenantId(settings.getTenetId())
                    .build();

            List<String> scopes = new ArrayList<>();
            scopes.add("https://graph.microsoft.com/.default");

            TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(scopes, clientSecretCredential);

            GraphServiceClient client = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
            return client;
        } catch (Exception e) {
            throw new Exception("Failed to create the Graph service client.");
        }

    }

    public void writeUsersToDB (Employee user) throws Exception {

        if(user.getEmail() != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("email").is(user.getEmail()));
            Optional<Employee> employee = Optional.ofNullable(mongoTemplate.findOne(query, Employee.class, USER_COLLECTION));
            if(employee.isPresent()) {
                throw new Exception("Employee already registered.");
            } else {
                mongoTemplate.save(user, USER_COLLECTION);
            }
        } else {
            throw new Exception("Email should not be empty.");
        }
    }

    public void updateEmployee(Token token, Employee employee) throws Exception {
        GraphServiceClient client = buildClient(token.getOrganizationId());

        User user = client.users(employee.getEmail()).buildRequest().get();
        if(employee.getFirstName() != null) {
            user.givenName = employee.getFirstName();
        }
        if(employee.getEmployeeId() != null) {
            user.employeeId = employee.getEmployeeId();
        }
        user = client.users(employee.getEmail()).buildRequest().patch(user);

    }

    public void getDepartment(Token token) throws Exception {
        GraphServiceClient client = buildClient(token.getOrganizationId());
        GroupCollectionPage groups = client.groups().buildRequest().get();
        System.out.println(groups.getCurrentPage().get(0).displayName);
    }

    public OnBoardParams addOrganization (OnBoardParams params){
        return mongoTemplate.save(params, ORG_COLLECTION);
    }

    public void addUserToAzureAD(Token token, Employee employee) throws Exception {

        DirectoryObject directoryObject = new DirectoryObject();
        directoryObject.id = employee.getEmployeeId();

        GraphServiceClient client = buildClient(token.getOrganizationId());
        client.groups(token.getUserId()).members().references().buildRequest().post(directoryObject);
    }

    public void removeUserFromAzureAD(Token token, Employee employee) throws Exception {
        GraphServiceClient client = buildClient(token.getOrganizationId());
        client.groups(token.getUserId()).members(employee.getEmployeeId()).reference().buildRequest().delete();
    }
}
