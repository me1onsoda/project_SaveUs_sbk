<<<<<<< HEAD
package com.example.Ex02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping({"/", "/dashboard"})
    public String main() {
        return "dashboard";
    }

    @GetMapping("/direct-input")
    public String directInput() {
        return "direct-input";
    }

    @GetMapping("/calendar")
    public String calendar() {
        return "calendar";
    }


}

=======
package com.example.Ex02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping({"/", "/dashboard"})
    public String main() {
        return "dashboard";
    }

    @GetMapping("/direct-input")
    public String directInput() {
        return "direct-input";
    }

    @GetMapping("/calendar")
    public String calendar() {
        return "calendar";
    }


}

>>>>>>> home2
