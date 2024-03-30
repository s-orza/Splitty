/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;


import commons.Password;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import server.api.PasswordController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
@EntityScan(basePackages = {"commons", "server" })
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        genPass();
    }

    private static void genPass()
    {
        final String uri = "http://localhost:8080/api/password";
        Password password = new Password();
        RestTemplate restTemplate = new RestTemplate();

        // delete all old passwords (0 or 1)
        restTemplate.delete(uri);

        // post new password
        ResponseEntity<Password> response = restTemplate.postForEntity(uri, password, Password.class);
    }
}