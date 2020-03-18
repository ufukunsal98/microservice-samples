package com.ufuk.accountprovider.Domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class User extends BasicEntity implements Serializable {

    @Id
    @NotNull
    private String id;
    @Field
    @NotNull`
    private String username;
    @Field
    @NotNull
    private String companyId;

    @Field
    @NotNull
    private String password;
    @NotNull
    private Boolean isEnabled;
    @Field
    private Boolean isVisible;
}
