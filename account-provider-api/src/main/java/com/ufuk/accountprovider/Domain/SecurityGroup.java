package com.ufuk.accountprovider.Domain;

@Document
@Data
@NoArgsConstructor
@Builder
public class SecurityGroup extends BasicEntity implements Serializable {

    @Id
    private String id;
    @NotNull
    @Field
    private String name;
    @Field
    private String description;
    @NotNull
    @Field
    private String companyId;
    @Field
    private List<String> users = new ArrayList<>();
    @Field
    private boolean removed = false;
}
