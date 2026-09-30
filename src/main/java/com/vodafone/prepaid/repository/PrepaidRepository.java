package com.vodafone.prepaid.repository;

import com.vodafone.prepaid.entity.PrepaidAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrepaidRepository extends JpaRepository<PrepaidAccount, Long> {

    Optional<PrepaidAccount> findByMsisdn(String msisdn);
    boolean existsByMsisdn(String msisdn);
}
