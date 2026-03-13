package com.abajin.innovation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.abajin.innovation")
public class InnovationApplication {

    public static void main(String[] args) {

        SpringApplication.run(InnovationApplication.class, args);
        System.out.println("   _____   _    _    _____    _____    _____    _____   _ \n" +
                "  / ____| | |  | |  / ____|  / ____|  / ____|  / ____| | |\n" +
                " | (___   | |  | | | |      | |      | (___   | (___   | |\n" +
                "  \\___ \\  | |  | | | |      | |       \\___ \\   \\___ \\  | |\n" +
                "  ____) | | |__| | | |____  | |____   ____) |  ____) | |_|\n" +
                " |_____/   \\____/   \\_____|  \\_____| |_____/  |_____/  (_)\n" +
                "                                                          \n" +
                "                                                          ");
    }

}
