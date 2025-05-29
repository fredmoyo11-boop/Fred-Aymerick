package com.sep.backend.route;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/route", produces = MediaType.APPLICATION_JSON_VALUE)
public class RouteController {

}
