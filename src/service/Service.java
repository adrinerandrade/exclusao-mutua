package service;

import messenger.*;
import messenger.Process;

import java.util.*;
import java.util.function.Consumer;

public class Service implements Process {

    private static final String ACTION_HEADER = "action";

    private TimeoutHandler timeoutHandler = new TimeoutHandler(this);
    private TreeSet<ServiceKey> allServices = new TreeSet<>();

    private final int rank;
    private final String id;
    private String coordinator;
    private boolean alive = true;

    public Service() {
        this.rank = ServiceIdProvider.newId();
        this.id = getProcessName(this.rank);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void onStart() {
        Payload payload = new Payload();
        payload.put(NewProcessMessage.RANK, this.rank);
        Message message = new Message(payload);
        message.addHeader(ACTION_HEADER, ServiceAction.NEW_PROCESS);
        timeoutHandler.waitForResponse(message).onTimeout(() -> {
            this.coordinator = this.id;
            CurrentCoordinator.setCoordinatorId(this.coordinator);
        });
        Messenger.broadcast(message);
    }

    @Override
    public void onMessage(Message message) {
        ServiceAction action = message.getHeader(ACTION_HEADER);
        ServiceMessage handler = action.getHandler();
        MessageType type = message.getType();
        switch (type) {
            case REQUEST:
                handler.getRequestExecutor(this).accept(message);
                break;
            case RESPONSE:
                timeoutHandler.answered(message);
                handler.getResponseExecutor(this).accept(message);
                break;
            default:
                System.out.print(String.format("\nMessage type no defined: %s.", type));
                break;
        }
    }

    private boolean isCoordinator() {
        return this.id.equals(this.coordinator);
    }

    void handleNewProcessRequest(Message message) {
        int sourceRank = message.getPayload().get(NewProcessMessage.RANK);
        mapNewProcess(message.getSourceProcessId(), sourceRank);
        Payload responsePayload = new Payload();
        responsePayload.put(NewProcessMessage.RANK, this.rank);
        responsePayload.put(NewProcessMessage.IS_COORDINATOR, this.isCoordinator());
        Messenger.send(new Reply(message).withPayload(responsePayload));
    }

    void handleNewProcessResponse(Message message) {
        Payload payload = message.getPayload();
        String sourceProcessId = message.getSourceProcessId();
        Integer sourceRank = payload.get(NewProcessMessage.RANK);
        mapNewProcess(sourceProcessId, sourceRank);
        boolean isCoordinator = payload.get(NewProcessMessage.IS_COORDINATOR);
        if (isCoordinator) {
            coordinator = sourceProcessId;
        }
    }

    public void messageToCoordinator() {
        if (coordinator == null) {
            System.out.print(String.format("\nProcess '%s' not ready yet to send message to coordinator.", getId()));
            return;
        }

        Message message = new Message(coordinator, new Payload());
        message.addHeader(ACTION_HEADER, ServiceAction.COORDINATOR_ACTION);
        timeoutHandler.waitForResponse(message).onTimeout(this::startElection);
        Messenger.sendFrom(id, message);
    }

    void handleCoordinatorActionRequest(Message message) {
        Payload response = new Payload();
        response.put(CoordinatorActionMessage.STATUS, "ok");
        Messenger.send(new Reply(message).withPayload(response));
    }

    void handleCoordinatorActionResponse(Message message) {
        String status = message.getPayload().get(CoordinatorActionMessage.STATUS);
        System.out.print(String.format("\nCoordinator status: %s.", status));
    }

    private void startElection() {
        if (alive) {
            System.out.print(String.format("\nElection started by process %s.", id));
            electionAction(new LinkedList<>());
        }
    }

    void handleElectionRequest(Message message) {
        LinkedList<Integer> pids = message.getPayload().get(ElectionMessage.PIDS);
        if (pids.contains(rank)) {
            coordinatorAction(pids);
        } else {
            electionAction(pids);
        }

        Messenger.send(new Reply(message).withoutPayload());
    }

    private void electionAction(LinkedList<Integer> pids) {
        executeIfHasSuccessor(successor -> {
            if (!pids.contains(rank)) {
                pids.add(rank);
            }
            Payload payload = new Payload();
            payload.put(ElectionMessage.PIDS, pids);
            Message message = new Message(successor.getServiceId(), payload);
            message.addHeader(ACTION_HEADER, ServiceAction.ELECTION);
            timeoutHandler.waitForResponse(message).onTimeout(() -> {
                System.out.print(String.format("\nSuccessor '%s' of '%s' inactive.", successor.getServiceId(), getId()));
                allServices.remove(successor);
                electionAction(pids);
            });
            Messenger.send(message);
        });
    }

    void handleElectionResponse(Message message) {
    }

    void handleCoordinatorRequest(Message message) {
        LinkedList<Integer> pids = message.getPayload().get(CoordinatorMessage.PIDS);
        if (!pids.getFirst().equals(rank)) {
            coordinatorAction(pids);
        }
        Messenger.send(new Reply(message).withoutPayload());
    }

    private void coordinatorAction(LinkedList<Integer> pids) {
        executeIfHasSuccessor(successor -> {
            pids.stream()
                    .min(Comparator.reverseOrder())
                    .ifPresent(higherPid -> {
                        this.coordinator = getProcessName(higherPid);
                        CurrentCoordinator.setCoordinatorId(this.coordinator);
                    });
            Payload payload = new Payload();
            payload.put(CoordinatorMessage.PIDS, pids);
            Message message = new Message(successor.getServiceId(), payload);
            message.addHeader(ACTION_HEADER, ServiceAction.COORDINATOR);
            timeoutHandler.waitForResponse(message).onTimeout(() -> {
                System.out.print(String.format("\nSuccessor '%s' of '%s' inactive.", successor.getServiceId(), getId()));
                allServices.remove(successor);
                coordinatorAction(pids);
            });
            Messenger.send(message);
        });
    }

    private void executeIfHasSuccessor(Consumer<ServiceKey> runnable) {
        Optional<ServiceKey> optionalSuccessor = getSuccessor();
        if (optionalSuccessor.isPresent()) {
            runnable.accept(optionalSuccessor.get());
        } else {
            System.out.print("\nNo successor found!");
            this.coordinator = id;
            CurrentCoordinator.setCoordinatorId(this.coordinator);
        }
    }

    private Optional<ServiceKey> getSuccessor() {
        return Optional.ofNullable(Optional.ofNullable(allServices.higher(new ServiceKey(id, rank)))
                .orElseGet(() -> {
                    try {
                        return allServices.first();
                    }catch (NoSuchElementException e) {
                        return null;
                    }
                })
        );
    }

    private String getProcessName(int pid) {
        return String.format("Process_%s", pid);
    }

    private void mapNewProcess(String sourceProcessId, int rank) {
        allServices.add(new ServiceKey(sourceProcessId, rank));
        System.out.print(String.format("\nAll mapped processes for %s: %s", id, allServices));
    }

    public void onKill() {
        timeoutHandler.clearALl();
        timeoutHandler = null;
        alive = false;
    }

}
