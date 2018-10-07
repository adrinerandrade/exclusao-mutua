package app;

import impl.ApplicationService;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Runner {

    public static void main(String[] args) {
        List<ApplicationService> services = Arrays.asList(
                new ApplicationService(3056),
                new ApplicationService(3055)
        );

        Scanner read = new Scanner(System.in);
        while(!"quit".equals(read.nextLine()));

        services.forEach(ApplicationService::stop);
    }

}
