package com.lacaja.servicios.controller;

import com.lacaja.servicios.service.AndreaniService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Raul Lapitzondo raul.lapitzondo@gmail.com -
 * Area de Arquitectura La Caja
 * @Date 9/4/22 13:29
 * @project meli2-onpremise-api-andreani-mn
 * @Version 1.0
 */
@Controller("/")
public class RequestAndreaniController {

    @Inject
    AndreaniService service;

    private static final Logger log = LoggerFactory.getLogger(RequestAndreaniController.class);

    @Post("/createOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_JSON)
    public HttpResponse<?> postOrder(@Body String pBody) {
        return HttpResponse.ok().body(service.postCreateOrder(pBody));
    }


    @Get("/token")
    @Produces(MediaType.TEXT_JSON)
    public HttpResponse<?>  token() {
        return HttpResponse.ok().body(service.getToken());
    }

    @Get("/label/{id}")
    @Produces(MediaType.TEXT_JSON)
    public HttpResponse<?>  getEtiqueta(String id) {
        return HttpResponse.ok().body(service.getEtiquetaService(id));
    }
}
