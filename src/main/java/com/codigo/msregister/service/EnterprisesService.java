package com.codigo.msregister.service;

import com.codigo.msregister.aggregates.request.RequestEnterprises;
import com.codigo.msregister.aggregates.response.ResponseBase;

public interface EnterprisesService {
    ResponseBase createEnterprise(RequestEnterprises requestEnterprises);
    ResponseBase findOneEnterprise(Integer id);
    ResponseBase  findAllEnterprises();
    ResponseBase updateEnterprise(Integer id, RequestEnterprises requestEnterprises);
    ResponseBase safeDeleteEnterprise(Integer id);
    ResponseBase permanentDeleteEnterprise(Integer id);
}
