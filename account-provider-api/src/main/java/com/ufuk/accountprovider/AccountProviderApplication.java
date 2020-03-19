package com.ufuk.accountprovider;

import com.ufuk.accountprovider.Entity.CustomClientDetails;
import com.ufuk.accountprovider.Repository.CustomClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class AccountProviderApplication {



    public static void main(String[] args) {
        SpringApplication.run(AccountProviderApplication.class , args);
    }


}
