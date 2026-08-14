

package pe.edu.upsjb.constancias.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upsjb.constancias.dto.*;
import pe.edu.upsjb.constancias.service.*;


@RestController


public class DocumentoController {


    @Autowired
    DocumentoService documentoService;


    @PostMapping (value = "/enviar-constancia-gratuidad")
    public ResponseEntity<byte[]> enviarConstanciaGratuidad (@RequestBody ConstanciaGratuidadRequest request) {

        byte[] pdf = documentoService.enviarConstanciaGratuidad(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("constancia-gratuidad.pdf").build());

        return ResponseEntity.ok().headers(headers).body(pdf);

    }

}

