package com.ufuk.accountprovider.Repository;


import com.ufuk.accountprovider.Domain.SecurityGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityGroupRepository extends JpaRepository<SecurityGroup, String> {


//    @Query("#{#n1ql.selectEntity} where #{#n1ql.filter} and companyId = $1 and removed = false " +
//            " AND ARRAY_CONTAINS(users, $2) ")
//    List<SecurityGroup> listUserGroups(String companyId, String userId);
}
