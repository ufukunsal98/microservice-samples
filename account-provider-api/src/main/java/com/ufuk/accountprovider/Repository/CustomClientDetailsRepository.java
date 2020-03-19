package com.ufuk.accountprovider.Repository;

import com.ufuk.accountprovider.Entity.CustomClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomClientDetailsRepository extends JpaRepository<CustomClientDetails, String> {

    CustomClientDetails findByClientId(String clientId);


}
