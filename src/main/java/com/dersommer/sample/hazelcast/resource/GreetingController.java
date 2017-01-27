package com.dersommer.sample.hazelcast.resource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.core.HazelcastInstance;

@RestController
public class GreetingController {
    private static Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);

    @Autowired
    HazelcastInstance hz;

    @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = { "application/json" })
    public Response greeting(@RequestParam(value = "name", required = false) String name) {
        Set<String> values = hz.getSet("values");

        values.add(name);

        Response response = new Response();
        
        response.setValues(new String[values.size()]);
        
        values.toArray(response.getValues());

        String result = Stream.of(values.toArray())
            .map(value -> (String) value)
            .collect(Collectors.joining(","));

        LOGGER.info("Result {}", result);

        return response;
    }

}
