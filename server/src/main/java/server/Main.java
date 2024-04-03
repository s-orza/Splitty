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


import com.fasterxml.jackson.databind.ObjectMapper;
import commons.AppConfig;
import commons.Password;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import server.service.MainService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

@SpringBootApplication
@EntityScan(basePackages = {"commons", "server"})
public class Main {

    public static void main(String[] args) {

        final MainService service = new MainService();
        SpringApplication app = new SpringApplication(Main.class);
        service.setAddress(app);
        app.run(args);
        service.genPass();
    }

}