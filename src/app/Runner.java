package app;

import impl.ApplicationService;

import java.util.Scanner;

public class Runner {

    public static void main(String[] args) {
        ApplicationService applicationService = new ApplicationService(3055);
        ApplicationService applicationService2 = new ApplicationService(3056);
        
        Scanner read = new Scanner(System.in);
        while(!"quit".equals(read.nextLine()));

        applicationService.stop();
        applicationService2.stop();
    }

}
