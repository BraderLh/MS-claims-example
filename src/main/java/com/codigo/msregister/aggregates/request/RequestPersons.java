package com.codigo.msregister.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestPersons {
    private String numDocument;
    private String email;
    private String telephone;
    private int documents_type_id_documents_type;
    private int enterprises_id_enterprises;
}
