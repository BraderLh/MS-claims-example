package com.codigo.msregister.service;

import com.codigo.msregister.aggregates.request.RequestPersons;
import com.codigo.msregister.aggregates.response.ResponseBase;

public interface PersonsService {
    ResponseBase getReniecInfo(String numero);
    ResponseBase createPersons(RequestPersons requestPerson);
    ResponseBase findOnePerson(int id);
    ResponseBase findAllPersons();
    ResponseBase updatePersons(int id, RequestPersons requestPersons);
    ResponseBase deletePerson(int id);
}
