package app;

import impl.ApplicationService;

import java.util.Scanner;

public class Runner {

    public static void main(String[] args) {
        ApplicationService applicationService = new ApplicationService(3050);

        Scanner read = new Scanner(System.in);
        while(!"quit".equals(read.nextLine()));

        applicationService.stop();
    }

}
