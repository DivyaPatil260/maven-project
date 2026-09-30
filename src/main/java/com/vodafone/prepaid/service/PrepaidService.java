package com.vodafone.prepaid.service;

import com.vodafone.prepaid.dto.*;
import com.vodafone.prepaid.entity.PrepaidAccount;

public interface PrepaidService {

    PrepaidAccount createAccount(PrepaidAccount account);

    PrepaidAccount getAccountByMsisdn(String msisdn);

    PrepaidAccount recharge(RechargeRequest request);

    PostpaidAccountDto getPostpaidInfoViaFeign(String msisdn);

    PostpaidBillDto getPostpaidBillViaRestTemplate(String msisdn);

    String initiatePlanMigrationSaga(MigrationRequestDto migrationRequest);
}
