package app;

import impl.ApplicationService;

import java.util.*;

public class Runner {

    private static List<ApplicationService> services = new ArrayList<>();
    private static int port = 3050;

    public static void main(String[] args) {
        Scanner read = new Scanner(System.in);

        List<Timer> timers = Arrays.asList(
                schedule(() -> newService(port++), 0, 40000),
                schedule(() -> getAnyNonCoordinatorService().ifPresent(service -> service.getResourceModule().requestResource()), 0, 40000),
                schedule(Runner::killCoordinator, 60000, 60000));

        while (!"quit".equals(read.nextLine())) ;

        timers.forEach(Timer::cancel);
        services.forEach(ApplicationService::stop);
    }

    private static void newService(int port) {
        services.add(new ApplicationService(port));
    }

    private static Optional<ApplicationService> getAnyNonCoordinatorService() {
        return services.stream()
                .filter(applicationService -> !applicationService.getCoordinatorModule().isCoordinator())
                .findAny();
    }

    private static void killCoordinator() {
        services.stream()
                .filter(applicationService -> applicationService.getCoordinatorModule().isCoordinator())
                .findAny()
                .orElseThrow(() -> new RuntimeException("Nenhum coordenador internada."))
                .stop();
    }

    private static Timer schedule(Runnable runnable, long delay, long period) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay, period);
        return timer;
    }

}
