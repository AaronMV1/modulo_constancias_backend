

package pe.edu.upsjb.constancias.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upsjb.constancias.dto.*;
import pe.edu.upsjb.constancias.dao.*;


@Service
@Transactional


public class DocumentoServiceImpl implements DocumentoService{


    @Autowired
    private DocumentoDao documentoDao;


    public byte[] enviarConstanciaGratuidad (ConstanciaGratuidadRequest request) {
        return documentoDao.enviarConstanciaGratuidad(request);
    }


}

